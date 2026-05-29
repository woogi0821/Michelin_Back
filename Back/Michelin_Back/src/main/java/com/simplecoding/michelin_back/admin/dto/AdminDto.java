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
        private String status;       // ACTIVE | SUSPENDED | WITHDRAWN
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

    /** 리뷰 목록 응답 — 프론트 스펙: id/writer/content/reportCount/createdAt/status */
    @Getter
    @Builder
    public static class ReviewResponse {
        private Long id;
        private String writer;       // 작성자 loginId
        private String content;
        private int reportCount;     // 신고 기능 미구현 → 0 고정
        private LocalDateTime createdAt;
        private String status;       // ACTIVE | REPORTED(=BLINDED) | DELETED
    }

    /** 문의 목록 응답 — 프론트 스펙: id/category/title/memberId/memberName/createdAt/status */
    @Getter
    @Builder
    public static class InquiryResponse {
        private Long id;
        private Long memberId;
        private String memberName;
        private String category;
        private String title;
        private String content;
        private String status;       // PENDING | ANSWERED
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

    /** 대시보드 통계 응답 — 프론트 스펙: newReviewsToday, recentRestaurants[restaurantId/name/category/regDate] */
    @Getter
    @Builder
    public static class DashboardStats {
        private long totalMembers;
        private long totalRestaurants;
        private long newReviewsToday;   // 필드명 todayNewReviews → newReviewsToday
        private long activePopups;
        private List<RecentRestaurant> recentRestaurants;
    }

    /** 최근 등록 매장 — 프론트 스펙 필드명 맞춤 */
    @Getter
    @Builder
    public static class RecentRestaurant {
        private Long restaurantId;
        private String name;
        private String category;
        private LocalDateTime regDate;
    }

    /** 관리자 목록 응답 */
    @Getter
    @Builder
    public static class AdminResponse {
        private Long adminId;
        private Long memberId;
        private String name;
        private String adminRole;    // MANAGER | SUPER
        private String adminPart;    // ALL | MEMBER | RESTAURANT | REVIEW | NOTICE | POPUP
    }

    /** 관리자 등록 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminCreateRequest {
        private Long memberId;
        private String adminRole;
        private String adminPart;
    }

    /** 관리자 역할 변경 요청 (쿼리 파라미터로 받으므로 별도 사용) */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminRoleRequest {
        private String newRole;
    }

    /** 관리자 - 레스토랑 목록 응답 */
    @Getter
    @Builder
    public static class RestaurantResponse {
        private Long id;
        private String restaurantName;
        private String grade;
        private String city;
        private String district;
        private String address;
        private String category;
        private String isGreenStar;
        private Integer viewCount;
        private String status;        // ACTIVE | DELETED
        private LocalDateTime createdAt;
    }
}
