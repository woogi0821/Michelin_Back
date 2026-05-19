package com.simplecoding.michelin_back.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

public class AdminLogDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Response {
        private Long adminLogId;
        private Long adminId;
        private Long targetId;
        private String adminAction;
        private String actionDetail;
        private LocalDateTime insertTime;
    }
}
