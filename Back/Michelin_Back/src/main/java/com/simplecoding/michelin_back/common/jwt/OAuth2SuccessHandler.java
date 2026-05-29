package com.simplecoding.michelin_back.common.jwt;

import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Value("${spring.react.ip}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // ✅ email 변수 선언 추가
        String email = extractEmail(oAuth2User);

        // 이메일 없으면 가짜 이메일 생성
        if (email == null || email.isEmpty()) {
            email = "kakao_" + oAuth2User.getName() + "@temp.com";
        }

        final String finalEmail = email;

        // DB에 없으면 가입 처리
        Member member = memberRepository.findByEmail(finalEmail)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .loginId(finalEmail)
                            .loginPw(java.util.UUID.randomUUID().toString())
                            .email(finalEmail)
                            .name("카카오유저")
                            .phone("010-0000-0000")
                            .provider("KAKAO")
                            .providerId(oAuth2User.getName())
                            .build();
                    return memberRepository.save(newMember);
                });

        // 토큰 발행 및 리다이렉트
        String accessToken  = tokenProvider.createAccessToken(member);
        String refreshToken = tokenProvider.createRefreshToken(member.getLoginId());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // ✅ extractEmail 메서드 추가
    @SuppressWarnings("unchecked")
    private String extractLoginId(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 카카오: attributes 최상위에 id(Long) 존재
        if (attributes.containsKey("kakao_account")) {
            return "kakao_" + attributes.get("id");
        }
        // 네이버: attributes.response.id(String)
        if (attributes.containsKey("response")) {
            Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
            return "naver_" + naverResponse.get("id");
        }
        throw new RuntimeException("지원하지 않는 OAuth2 Provider 입니다.");
    }
}