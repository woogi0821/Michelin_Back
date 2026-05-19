package com.simplecoding.michelin_back.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {}

    /**
     * 현재 로그인한 사용자의 loginId 반환
     * (JWT subject = loginId)
     */
    public static String getCurrentLoginId() {
        return getAuthentication().getName();
    }

    /**
     * 기존 코드와의 호환성 유지 — getCurrentLoginId() 와 동일
     */
    public static String getCurrentEmail() {
        return getCurrentLoginId();
    }

    /**
     * 현재 로그인한 사용자의 memberId 반환
     * CustomUserDetails 가 principal 인 경우에만 사용 가능
     */
    public static Long getCurrentMemberId() {
        Authentication auth = getAuthentication();
        if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getMemberId();
        }
        throw new CommonException(
                org.springframework.http.HttpStatus.UNAUTHORIZED,
                "인증 정보에서 memberId를 추출할 수 없습니다."
        );
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CommonException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "로그인이 필요합니다."
            );
        }
        return authentication;
    }
}
