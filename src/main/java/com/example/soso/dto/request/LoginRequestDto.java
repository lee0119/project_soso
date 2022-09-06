package com.example.soso.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank// null값과 빈 공백 문자열을 허용하지 않는 어노테이션
    private String username; //로그인 아이디

    @NotBlank// null값과 빈 공백 문자열을 허용하지 않는 어노테이션
    private String password; //로그인 비밀번호

}