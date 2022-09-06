package com.example.soso.controller;


import com.example.soso.controller.request.PostHeartDto;
import com.example.soso.service.PostHeartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/like/{id}")
public class PostHeartController {
    private final PostHeartService heartService;

    @PutMapping //일단 put으로 해봄 post로 해야할지도 모름
    public ResponseEntity<PostHeartDto> heart(@RequestBody PostHeartDto heartDto) {
        heartService.plusHeart(heartDto);
        return new ResponseEntity<>(heartDto, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<PostHeartDto> unHeart(@RequestBody  PostHeartDto heartDto) {
        heartService.minusHeart(heartDto);
        return new ResponseEntity<>(heartDto, HttpStatus.OK);
    }
}
