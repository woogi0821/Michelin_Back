package com.simplecoding.michelin_back.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class DailyStatsDto {

    @Getter
    @Builder
    public static class Response {
        private LocalDateTime statsDate;
        private Long newMembers;
        private Long newReviews;
        private Long newPenalties;
        private Long totalInquiries;
        private Long answeredInquiries;
        private Long chatbotSessions;
        private Long totalTokens;
    }
}
