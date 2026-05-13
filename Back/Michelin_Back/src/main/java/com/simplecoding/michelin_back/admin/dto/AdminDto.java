package com.simplecoding.michelin_back.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AdminDto {

    @Getter
    @NoArgsConstructor
    public static class Request{
        @NotNull
        private Long memberId;
        private String adminRole;
    }

    @Getter
    @Builder
    public static class Response{
        private Long adminId;
        private String memberName;
        private String adminRole;
    }
}
