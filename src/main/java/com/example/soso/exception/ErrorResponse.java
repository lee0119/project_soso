package com.example.soso.exception;

<<<<<<< HEAD

=======
>>>>>>> 662a55560bc07d664388a66946b308995fba5354
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Builder
@Getter
public class ErrorResponse {

<<<<<<< HEAD
=======
    private final int status;
    private final String error;
    private final String code;
    private final String message;


>>>>>>> 662a55560bc07d664388a66946b308995fba5354
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
<<<<<<< HEAD
                        .code(errorCode.name())
                        .message(errorCode.getDetail())
                        .build()
                );
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode,
                                                                 String detail) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .message(detail)
                        .build()
                );
    }

}
=======
                        .code(errorCode.getErrorCode())
                        .message(errorCode.getErrorMessage())
                        .build());
    }

}
>>>>>>> 662a55560bc07d664388a66946b308995fba5354
