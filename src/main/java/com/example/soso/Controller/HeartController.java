package com.example.soso.controller;


import com.example.soso.dto.response.ResponseDto;
import com.example.soso.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class HeartController {

    private final HeartService heartService;

    @RequestMapping(value = "/api/auth/like/{id}", method = RequestMethod.POST)
    public ResponseDto<?> postLike(@PathVariable Long id, HttpServletRequest request) {
        return heartService.postLike(id,request);
    }


    @RequestMapping(value = "/api/auth/like/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> postDisLike(@PathVariable Long id, HttpServletRequest request) {
        return heartService.postDisLike(id,request);
    }


}
