package com.simplecoding.michelin_back.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

public class InquiryDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Response {
        private Long inquiryId;
        private Long memberId;
        private String memberName;
        private String title;
        private String content;
        private String status;
        private String answer;
        private LocalDateTime answeredAt;
        private LocalDateTime insertTime;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String title;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerRequest {
        private String answer;
    }
}
