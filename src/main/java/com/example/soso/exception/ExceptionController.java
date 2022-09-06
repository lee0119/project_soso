package com.example.soso.exception;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {
        ObjectError objectError = Objects.requireNonNull(
                exception.getBindingResult().getAllErrors().stream().findFirst()
                        .orElse(null));
        log.error("handleValidationException throw MethodArgumentNotValidException : {}",
                ErrorCode.INVALID_FIELD);
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_FIELD, objectError.getDefaultMessage());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadableException() {
        log.error("handleNotReadableException throw Exception : {}", ErrorCode.WRONG_OBJECT);
        return ErrorResponse.toResponseEntity(ErrorCode.WRONG_OBJECT);
    }

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

}
