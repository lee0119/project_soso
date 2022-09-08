package com.example.soso.service;


import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.example.soso.domain.Member;
import com.example.soso.domain.RefreshToken;
import com.example.soso.dto.request.LoginRequestDto;
import com.example.soso.dto.request.MemberRequestDto;
import com.example.soso.dto.request.TokenDto;
import com.example.soso.dto.response.MemberResponseDto;
import com.example.soso.dto.response.ResponseDto;
import com.example.soso.jwt.TokenProvider;
import com.example.soso.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> createMember(MemberRequestDto requestDto) {
        if (null != isPresentMember(requestDto.getUsername())) {
            return ResponseDto.fail("DUPLICATED_USERNAME",

                    "username is duplicated");
        }

        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            return ResponseDto.fail("PASSWORDS_NOT_MATCHED",

                    "password and password confirm are not matched");
        }

        Member member = Member.builder()
                .username(requestDto.getUsername())
                .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .build();
        memberRepository.save(member);
        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(member.getId())
                        .username((member.getUsername()))
                        .nickname(member.getNickname())
                        .createdAt(member.getCreatedAt())
                        .modifiedAt(member.getModifiedAt())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        Member member = isPresentMember(requestDto.getUsername());
        if (null == member) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "member not found");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(member.getId())
                        .username(member.getUsername())
                        .nickname(member.getNickname())
                        .createdAt(member.getCreatedAt())
                        .modifiedAt(member.getModifiedAt())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
        }
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "member not found");
        }

        Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Authorization"));
        RefreshToken refreshToken = tokenProvider.isPresentRefreshToken(member);

        if (!refreshToken.getRefreshToken().equals(request.getHeader("RefreshToken"))) {
            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        refreshToken.updateValue(tokenDto.getRefreshToken());
        tokenToHeaders(tokenDto, response);
        return ResponseDto.success("success");
    }

    public ResponseDto<?> logout(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
        }
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "member not found");
        }

        return tokenProvider.deleteRefreshToken(member);
    }

    @Transactional(readOnly = true)
    public Member isPresentMember(String username) {
        Optional<Member> optionalMember = memberRepository.findByUsername(username);
        return optionalMember.orElse(null);
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("RefreshToken", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

}



//import com.example.soso.dto.request.LoginRequestDto;
//import com.example.soso.dto.request.MemberRequestDto;
//import com.example.soso.dto.request.TokenDto;
//import com.example.soso.dto.response.MemberResponseDto;
//import com.example.soso.dto.response.ResponseDto;
//import com.example.soso.domain.Member;
//import com.example.soso.jwt.TokenProvider;
//import com.example.soso.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//@Service
//public class MemberService {
//
//    private final MemberRepository memberRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final TokenProvider tokenProvider;
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
//
//    @Transactional
//    public ResponseDto<?> createMember(MemberRequestDto requestDto) {
//        if (null != isPresentMember(requestDto.getUsername())) {
//            return ResponseDto.fail("DUPLICATED_NICKNAME",
//                    "중복된 아이디 입니다.");
//        }
//
//        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
//            return ResponseDto.fail("PASSWORDS_NOT_MATCHED",
//                    "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
//        }
//
//        Member member = Member.builder()
//                .username(requestDto.getUsername())
//                .nickname(requestDto.getNickname())
//                .password(passwordEncoder.encode(requestDto.getPassword()))
//                .build();
//        memberRepository.save(member);
//        return ResponseDto.success(
//                MemberResponseDto.builder()
//                        .id(member.getId())
//                        .username(member.getUsername())
//                        .nickname(member.getNickname())
//                        .createdAt(member.getCreatedAt())
//                        .modifiedAt(member.getModifiedAt())
//                        .build()
//        );
//    }
//
//    @Transactional
//    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
//        Member member = isPresentMember(requestDto.getUsername());
//        if (null == member) {
//            return ResponseDto.fail("MEMBER_NOT_FOUND",
//                    "사용자를 찾을 수 없습니다.");
//        }
//
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword());
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//
//
//        if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
//            return ResponseDto.fail("INVALID_MEMBER", "사용자를 찾을 수 없습니다.");
//        }
//
//
//        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
//        tokenToHeaders(tokenDto, response);
//
//        return ResponseDto.success(
//                MemberResponseDto.builder()
//                        .id(member.getId())
//                        .username(member.getUsername())
//                        .nickname(member.getNickname())
//                        .createdAt(member.getCreatedAt())
//                        .modifiedAt(member.getModifiedAt())
//                        .build()
//        );
//    }
//
//
//    public ResponseDto<?> logout(HttpServletRequest request) {
//        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
//            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
//        }
//        Member member = tokenProvider.getMemberFromAuthentication();
//        if (null == member) {
//            return ResponseDto.fail("MEMBER_NOT_FOUND",
//                    "사용자를 찾을 수 없습니다.");
//        }
//
//        return tokenProvider.deleteRefreshToken(member);
//    }
//
//    @Transactional(readOnly = true)
//    public Member isPresentMember(String username) {
//        Optional<Member> optionalMember = memberRepository.findByUsername(username);
//        return optionalMember.orElse(null);
//    }
//
//
//    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
//        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
//        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
//        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
//    }
//
//}