package com.simplecoding.michelin_back.security;

import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId;
        String email;
        String name;

        if ("kakao".equals(registrationId)) {
            providerId = String.valueOf(attributes.get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            email = (String) kakaoAccount.getOrDefault("email", providerId + "@kakao.com");
            name = (String) profile.getOrDefault("nickname", "카카오유저");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            providerId = (String) response.get("id");
            email = (String) response.getOrDefault("email", providerId + "@naver.com");
            name = (String) response.getOrDefault("name", "네이버유저");
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }

        // 기존 회원 조회 또는 신규 가입
        String finalEmail = email;
        String finalName = name;
        String finalProviderId = providerId;

        memberRepository.findByLoginId(registrationId + "_" + providerId)
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .loginId(registrationId + "_" + finalProviderId)
                                .loginPw("")
                                .email(finalEmail)
                                .name(finalName)
                                .provider(registrationId.toUpperCase())
                                .providerId(finalProviderId)
                                .build()
                ));

        return oAuth2User;
    }
}