package com.simplecoding.michelin_back.auth.controller;

import com.simplecoding.michelin_back.auth.dto.AuthDto;
import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.CommonException;
import com.simplecoding.michelin_back.common.jwt.JwtTokenProvider;
import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    /**
     * Refresh Token(쿠키) → 새 Access Token 발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthDto.TokenResponse>> refresh(HttpServletRequest request) {
        String refreshToken = extractRefreshToken(request);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw CommonException.unauthorized("유효하지 않은 Refresh Token입니다. 다시 로그인해주세요.");
        }

        String loginId = jwtTokenProvider.getLoginId(refreshToken);
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));

        String newAccessToken = jwtTokenProvider.createAccessToken(member);

        return ResponseEntity.ok(ApiResponse.success(
                AuthDto.TokenResponse.builder()
                        .accessToken(newAccessToken)
                        .tokenType("Bearer")
                        .build()
        ));
    }

    /**
     * 로그아웃 — Refresh Token 쿠키 만료 처리
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        Cookie expiredCookie = new Cookie("refreshToken", null);
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        expiredCookie.setHttpOnly(true);
        response.addCookie(expiredCookie);

        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다."));
    }

    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
