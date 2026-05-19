package com.simplecoding.michelin_back.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

public class NoticeDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Response {
        private Long noticeId;
        private String title;
        private String content;
        private String fixYn;
        private String deletYn;
        private Long writerId;
        private LocalDateTime insertTime;
        private LocalDateTime updateTime;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String title;
        private String content;
        private String fixYn;  // "Y" or "N"
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String content;
        private String fixYn;
    }
}
