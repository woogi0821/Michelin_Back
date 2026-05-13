package com.simplecoding.michelin_back.admin.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NoticeDto {

    @Getter
    @NoArgsConstructor
    public static class Request{
        @NotBlank
        private String title;
        @NotBlank
        private String content;
        private String fixYn = "N";
    }

    @Getter
    @Builder
    public static class ListResponse{
        private Long noticeId;
        private String title;
        private String fixYn;
        private String writerName;
        private LocalDateTime insertTime;
        private LocalDateTime updateTime;
    }
}
