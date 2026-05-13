package com.simplecoding.michelin_back.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class AdminLogDto {

    @Getter
    @Builder
    public static class Response{
        private Long logId;
        private String adminName;
        private String action;
        private String targetType;
        private String targetId;
        private String detail;
        private LocalDateTime createdAt;
    }
}
