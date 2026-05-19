package com.simplecoding.michelin_back.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

public class PenaltyHistoryDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Response {
        private Long penaltyId;
        private Long memberId;
        private String memberName;
        private Long reviewId;
        private Long adminId;
        private String penaltyReason;
        private String penaltyType;
        private Integer suspendDays;
        private LocalDateTime insertTime;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long memberId;
        private Long reviewId;
        private String penaltyReason;
        private String penaltyType;
        private Integer suspendDays;
    }
}
