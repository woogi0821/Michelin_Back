package com.simplecoding.michelin_back.admin.dto;

import lombok.*;

public class AdminDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Response {
        private Long adminId;
        private Long memberId;
        private String loginId;
        private String name;
        private String adminRole;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrantRequest {
        private Long memberId;
        private String adminRole;
    }
}
