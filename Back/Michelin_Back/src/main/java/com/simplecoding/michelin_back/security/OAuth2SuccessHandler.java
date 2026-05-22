package com.simplecoding.michelin_back.security;

import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 카카오/네이버 loginId 형식: "kakao_123456" or "naver_abcdef"
        String loginId = oAuth2User.getAttributes().values().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("");

        Member member = memberRepository.findByLoginId(loginId).orElse(null);

        if (member != null) {
            String accessToken = jwtTokenProvider.createAccessToken(member.getLoginId());
            // 프론트로 토큰 전달 (쿼리파라미터 방식)
            String redirectUrl = "http://localhost:5173/oauth2/callback?token=" + accessToken;
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect("http://localhost:5173/login?error=oauth2");
        }
    }
}