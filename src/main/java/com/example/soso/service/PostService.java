package com.example.soso.service;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.soso.domain.Member;
import com.example.soso.domain.Post;
import com.example.soso.domain.S3Uploader;
import com.example.soso.dto.request.MemberRequestDto;
import com.example.soso.dto.request.PostRequestDto;
import com.example.soso.dto.response.PostResponseDto;
import com.example.soso.dto.response.ResponseDto;
import com.example.soso.jwt.TokenProvider;
import com.example.soso.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final S3Uploader s3Uploader;
    private final TokenProvider tokenProvider;


    // 전체 목록 조회 처리
    @Transactional
    public ResponseDto<?> getAllPosts() {
        return ResponseDto.success(postRepository.findAllByOrderByCreatedAtDesc());
    }


    // 게시글 상세페이지 처리
    @Transactional
    public ResponseDto<?> getPost(Long id) {
        Post post = isPresentPost(id);
        if(null == post) {
            return ResponseDto.fail("NOT_FOUND", "게시글이 존재하지 않습니다.");
        }

        return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .nickName(post.getMember().getNickname())
                    .imageUrl(post.getImageUrl())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
        );

    }

    // 게시글 등록 처리
    @Transactional
    public ResponseDto<?> createPost(PostRequestDto postRequestDto, MultipartFile multipartFile, HttpServletRequest request) throws IOException {

        // 토큰을 가지고 있는지 확인
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
        }

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
                .member(member)
                .imageUrl(imageUrl)
                .build();
        postRepository.save(post);

        return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .nickName(post.getMember().getNickname())
                    .imageUrl(post.getImageUrl())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build());
    }

    // 게시글 수정 처리
    @Transactional
    public ResponseDto<?> updatePost(Long id, PostRequestDto postRequestDto, MultipartFile multipartFile, HttpServletRequest request) throws IOException {


        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "토큰이 유효하지 않습니다.");
        }
        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "게시글이 존재하지 않습니다.");
        }
        if (post.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }


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
        return ResponseDto.success(
            PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .nickName(post.getMember().getNickname())
                .imageUrl(post.getImageUrl())
                .modifiedAt(post.getModifiedAt())
                .build());

    }

    // 게시글 및 이미지 삭제 처리
    @Transactional
    public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "토큰이 유효하지 않습니다.");
        }
        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "게시글이 존재하지 않습니다.");
        }
        if (post.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
        }


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

        return ResponseDto.success("삭제 완료");
    }


    // 게시글 존재 여부 확인
    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }


    // 토큰 소유 여부 확인
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
