package com.project.soso.service;


import com.project.soso.dto.PostRequestDto;
import com.project.soso.dto.PostResponseDto;
import com.project.soso.entity.Post;
import com.project.soso.entity.S3Uploader;
import com.project.soso.entity.UploadImage;
import com.project.soso.repository.ImageRepository;
import com.project.soso.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
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

        String imgUrl = s3Uploader.upload(multipartFile, "soso");

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .nickname(postRequestDto.getNickname())
                .imgUrl(imgUrl)
                .build();
        postRepository.save(post);

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .nickname(post.getNickname())
                .imgUrl(post.getImgUrl())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }

    // 게시글 수정 처리
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto, MultipartFile multipartFile) throws IOException {

        String imgUrl = s3Uploader.upload(multipartFile, "soso");

        Post post = postRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        post.update(postRequestDto);
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .nickname(post.getNickname())
                .imgUrl(post.getImgUrl())
                .modifiedAt(post.getModifiedAt())
                .build();
    }

    // 게시글 및 이미지 삭제 처리
    @Transactional
    public PostResponseDto deletePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        String temp = post.getImgUrl();
        UploadImage uploadImage = imageRepository.findByImgUrl(temp);
        String filename = uploadImage.getFilename();
        s3Uploader.deleteImage(filename);

        return null;
    }

}
