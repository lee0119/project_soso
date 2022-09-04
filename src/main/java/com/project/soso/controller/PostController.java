package com.project.soso.controller;

import com.project.soso.dto.PostRequestDto;
import com.project.soso.dto.PostResponseDto;
import com.project.soso.entity.Post;
import com.project.soso.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;


    // 전체 목록 조회(메인 페이지)
    @GetMapping("/api/auth/post")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    // 게시글 상세 페이지
    @GetMapping("/api/auth/post/{id}")
    public Post getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 게시글 등록
    @PostMapping(value = "/api/auth/post", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public PostResponseDto createPost(@RequestPart PostRequestDto postRequestDto, @RequestPart(required = false) MultipartFile multipartFile) throws IOException {

        return postService.createPost(postRequestDto, multipartFile);

    }

    // 게시글 수정
    @PutMapping(value = "/api/auth/post/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public PostResponseDto updatePost(@PathVariable Long id, @RequestPart PostRequestDto postRequestDto, @RequestPart(required = false) MultipartFile multipartFile) throws IOException {
        return postService.updatePost(id, postRequestDto, multipartFile);
    }

    // 게시글 삭제
    @DeleteMapping("/api/auth/post/{id}")
    public PostResponseDto deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }

}
