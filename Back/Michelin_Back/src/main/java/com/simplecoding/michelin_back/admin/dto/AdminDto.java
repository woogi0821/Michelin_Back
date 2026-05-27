package com.simplecoding.michelin_back.admin.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDto {

    /** 회원 목록 응답 */
    @Getter
    @Builder
    public static class MemberResponse {
        private Long memberId;
        private String loginId;
        private String name;
        private String email;
        private String phone;
        private String status;
        private String memberGrade;
        private String provider;
        private Integer penaltyCount;
        private LocalDate suspendedUntil;
        private LocalDateTime insertTime;
    }

    /** 회원 정지 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuspendRequest {
        private LocalDate suspendedUntil;
    }

    /** 리뷰 목록 응답 */
    @Getter
    @Builder
    public static class ReviewResponse {
        private Long reviewId;
        private Long restaurantId;
        private Long memberId;
        private String content;
        private Integer rating;
        private String isDeleted;
        private String isBlinded;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /** 문의 목록 응답 */
    @Getter
    @Builder
    public static class InquiryResponse {
        private Long inquiryId;
        private Long memberId;
        private String category;
        private String title;
        private String content;
        private String status;
        private String answer;
        private LocalDateTime answeredAt;
        private LocalDateTime createdAt;
    }

    /** 문의 답변 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerRequest {
        private String answer;
    }

    /** 대시보드 통계 응답 */
    @Getter
    @Builder
    public static class DashboardStats {
        private long totalMembers;
        private long totalRestaurants;
        private long todayNewReviews;
        private long activePopups;
        private List<RecentRestaurant> recentRestaurants;
    }

    /** 최근 등록 매장 */
    @Getter
    @Builder
    public static class RecentRestaurant {
        private Long id;
        private String restaurantName;
        private String city;
        private String grade;
    }
}
