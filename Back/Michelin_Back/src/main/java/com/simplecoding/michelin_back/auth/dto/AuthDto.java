package com.simplecoding.michelin_back.auth.dto;

import lombok.*;

public class AuthDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String tokenType;
    }
}
