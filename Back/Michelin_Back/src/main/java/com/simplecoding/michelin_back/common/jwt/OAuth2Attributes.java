package com.simplecoding.michelin_back.common.jwt;

import com.simplecoding.michelin_back.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuth2Attributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String phone;      // 가능한 경우 추출, 없으면 ""
    private String provider;
    private String providerId;

    public static OAuth2Attributes of(String registrationId, String userNameAttributeName,
                                      Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }
        return ofKakao(userNameAttributeName, attributes);
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        // 전화번호: scope에 phone_number 포함 시 제공 (+82 10-xxxx-xxxx → 정규화)
        String rawPhone = (String) kakaoAccount.getOrDefault("phone_number", "");
        String phone = normalizePhone(rawPhone);

        return OAuth2Attributes.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .phone(phone)
                .provider("KAKAO")
                .providerId(String.valueOf(attributes.get(userNameAttributeName)))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        // 전화번호: scope에 mobile 포함 시 제공 (+82-10-xxxx-xxxx → 정규화)
        String rawPhone = (String) response.getOrDefault("mobile", "");
        String phone = normalizePhone(rawPhone);

        return OAuth2Attributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .phone(phone)
                .provider("NAVER")
                .providerId((String) response.get(userNameAttributeName))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    /**
     * "+82-10-1234-5678" / "+82 10-1234-5678" → "010-1234-5678"
     * 제공되지 않으면 빈 문자열 반환 (DB NOT NULL 우회)
     */
    private static String normalizePhone(String raw) {
        if (raw == null || raw.isBlank()) return "";
        // +82 국가코드 제거 후 앞자리 0 붙이기
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.startsWith("82") && digits.length() >= 11) {
            digits = "0" + digits.substring(2);
        }
        // 11자리면 하이픈 포맷으로
        if (digits.length() == 11) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
        }
        return digits.length() > 0 ? digits : "";
    }

    public Member toEntity() {
        return Member.builder()
                .loginId(provider + "_" + providerId)
                .loginPw("")
                .name(name)
                .email(email)
                .phone(phone)          // 정규화된 전화번호 (없으면 "")
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
