package com.simplecoding.michelin_back.admin.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyStatsDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Response {
        private Long statId;
        private LocalDate statDate;
        private Long totalMembers;
        private Long newMembers;
        private Long activeReviews;
        private Long totalInquiries;
        private Long pendingInquiries;
        private LocalDateTime insertTime;
    }

    /** 대시보드 요약용 */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Summary {
        private LocalDate today;
        private Long totalMembers;
        private Long newMembersToday;
        private Long activeReviews;
        private Long pendingInquiries;
    }
}
