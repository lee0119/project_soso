package com.example.soso.domain;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    // 1. MultipartFile을 전달받아 File로 전환한 후에 S3에 업로드
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        // 업로드 되는 파일이 이미지 파일인지 여부 검증
        isImage(multipartFile);

        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("파일 변환에 실패하였습니다."));

        return upload(uploadFile, dirName);
    }

    // 2. S3에 파일 업로드 하기
    //    fileName = S3에 저장되는 파일이름(randomUUID는 파일이 덮어씌워지지 않기 위함)
    //    1번을 진행하면서 로컬에 생성된 파일을 삭제까지 하는 프로세스
    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);

        return uploadImageUrl;
    }


    // 1-1. 로컬에 파일 저장하기! MultipartFile에서 File로 변환함
    //      getProperty(이곳) 이곳에 File이 생성됨(경로가 잘못되면 생성 불가능)
    //      FileOutputStream : 데이터를 바이트스트림으로 저장하는 객체(파일 주고받을 때 바이트스트림 사용)
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());

            } return Optional.of(convertFile);

        } return Optional.empty();
    }


    // 2-1. PublicRead 권한으로 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }


    // 2-2. 로컬에 저장된 이미지 삭제
    private void removeNewFile(File targetFile) {
        if(targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
            return;
        } log.info("삭제가 실패하였습니다.");
    }


    // 이미지 파일인지 확인하는 메소드
    private void isImage(MultipartFile multipartFile) throws IOException {

        // tika를 이용해 파일 MIME 타입 체크
        // 파일명에 .jpg 식으로 붙는 확장자는 없앨 수도 있고 조작도 가능하므로 MIME 타입을 체크하는 것이 좋다.
        Tika tika = new Tika();
        String mimeType = tika.detect(multipartFile.getInputStream());

        // MIME타입이 이미지가 아니면 exception 발생
        if (!mimeType.startsWith("image/")) {
            throw new IllegalStateException("이미지 파일이 아닙니다");
        }
    }

}
