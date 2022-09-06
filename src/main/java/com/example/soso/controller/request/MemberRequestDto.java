package com.example.soso.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

    @NotBlank // null값과 빈 공백 문자열을 허용하지 않는 어노테이션
    @Size(min = 4, max = 12) // 문자열의 길이를 적용하는 어노테이션
    @Pattern(regexp = "[a-zA-Z\\d]*${3,12}") // 문자 정규식을 지정하는 어노테이션
    private String username; //회원가입 아이디

    @NotBlank
    @Size(min = 2, max = 6)
    private String nickname;

    @NotBlank// null값과 빈 공백 문자열을 허용하지 않는 어노테이션
    @Size(min = 4, max = 32)// 문자열의 길이를 적용하는 어노테이션
    @Pattern(regexp = "[a-z\\d]*${3,32}")// 문자 정규식을 지정하는 어노테이션
    private String password; //회원가입 비밀번호

    @NotBlank// null값과 빈 공백 문자열을 허용하지 않는 어노테이션
    private String passwordConfirm; //회원가입 비밀번호확인
}