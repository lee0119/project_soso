package com.example.soso.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    FAILURE_CONVERSION_FILE(HttpStatus.OK, "200_1", "파일 변환에 실패하였습니다!"),
    NOT_EXIST_POST(HttpStatus.BAD_REQUEST, "400_1", "게시글이 존재하지 않습니다!");


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}

