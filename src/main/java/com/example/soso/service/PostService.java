package com.example.soso.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.soso.dto.request.PostRequestDto;
import com.example.soso.domain.Post;
import com.example.soso.domain.S3Uploader;
import com.example.soso.dto.response.PostResponseDto;
import com.example.soso.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final S3Uploader s3Uploader;


    // 전체 목록 조회 처리
    @Transactional
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    // 게시글 상세페이지 처리
    @Transactional
    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

    }

    // 게시글 등록 처리
    @Transactional
    public PostResponseDto createPost(PostRequestDto postRequestDto, MultipartFile multipartFile) throws IOException {

        // 업로드 파일이 없으면 URL과 filename(key값)을 null로 반환하고 있으면 URL과 filename(key값) 생성해서 저장하기
        // filename(key값)은 삭제를 위해 저장이 필요함
        String imageUrl;
        String fileName;

        if(multipartFile.isEmpty()) {
            imageUrl = null;
            fileName = null;
        } else {
            imageUrl = s3Uploader.upload(multipartFile, "soso");
            fileName = imageUrl.substring(imageUrl.indexOf("soso"));
        }

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .fileName(fileName)
                .imageUrl(imageUrl)
                .build();
        postRepository.save(post);

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }

    // 게시글 수정 처리
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto, MultipartFile multipartFile) throws IOException {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        String imageUrl;
        String fileName;

        // 새로운 이미지 파일 등록여부 조건문
        if(multipartFile.isEmpty()) {
            imageUrl = post.getImageUrl();
            fileName = post.getFileName();
        } else {
            // 기존에 이미지 파일이 존재하는 경우, 기존 이미지 파일 삭제
            if(post.getImageUrl() != null) {
                final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("ap-northeast-2").build();
                s3.deleteObject("postblog-bucket", post.getFileName());
            }

            imageUrl = s3Uploader.upload(multipartFile, "soso");
            fileName = imageUrl.substring(imageUrl.indexOf("soso"));

        }

        PostRequestDto updatePost = PostRequestDto.builder()
                .title(postRequestDto.getTitle())
                .imageUrl(imageUrl)
                .fileName(fileName)
                .build();

        post.update(updatePost);
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .imageUrl(post.getImageUrl())
                .modifiedAt(post.getModifiedAt())
                .build();
    }

    // 게시글 및 이미지 삭제 처리
    @Transactional
    public PostResponseDto deletePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 업로드 파일이 있으면 업로드 파일 먼저 지우기
        if(post.getImageUrl() != null) {
            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("ap-northeast-2").build();
            try {
                s3.deleteObject("postblog-bucket", post.getFileName());

            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
                System.exit(1);
            }

            System.out.println("삭제완료!");
        }
        postRepository.delete(post);

        return null;
    }
}