package com.simplecoding.michelin_back.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class InquiryDto {

    @Getter
    @NoArgsConstructor
    public static class AnswerRequest{
        @NotBlank
        private String answerContent;
    }

    @Getter
    @Builder
    public static class ListResponse{
        private Long inquiryId;
        private String category;
        private String title;
        private String status;
        private String memberName;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class DetailResponse{
        private Long inquiryId;
        private String category;
        private String title;
        private String status;
        private String memberName;
        private String answerContent;
        private LocalDateTime answerAt;
        private LocalDateTime createdAt;
    }
}
