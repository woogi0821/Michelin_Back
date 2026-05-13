package com.simplecoding.michelin_back.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PenaltyHistoryDto {

    @Getter
    @NoArgsConstructor
    public static class Request{
        @NotNull
        private Long memberId;
        @NotBlank
        private String reason;
    }

    @Getter
    @Builder
    public static class Response{
        private Long penaltyId;
        private String memberName;
        private String adminName;
        private String reason;
        private String status;
        private LocalDateTime createdAt;
    }
}
