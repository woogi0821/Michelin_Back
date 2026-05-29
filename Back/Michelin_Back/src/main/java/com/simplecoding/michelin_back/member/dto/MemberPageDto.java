package com.simplecoding.michelin_back.member.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class MemberPageDto {

    /** GET /api/v1/members/me — 프로필 + 카운트 */
    @Getter
    @Builder
    public static class ProfileResponse {
        private Long memberId;
        private String name;
        private String email;
        private String loginId;
        private String provider;
        private String status;
        private long reviewCount;
        private long likeCount;
        private long bookmarkCount;
        private long visitCount;   // 리뷰 남긴 매장 수 (중복 제거)
    }

    /**
     * GET /api/v1/members/me/michelin-stats — 등급별 방문 현황
     * 프론트 스펙: 배열로 반환, 각 항목은 label/grade/visited/total/color/borderColor
     */
    @Getter
    @Builder
    public static class MichelinStatItem {
        private String label;
        private String grade;
        private long visited;
        private long total;
        private String color;
        private String borderColor;
    }

    /** GET /api/v1/members/me/reviews — 내 리뷰 목록 */
    @Getter
    @Builder
    public static class MyReviewResponse {
        private Long reviewId;
        private Long restaurantId;
        private String restaurantName;
        private String content;
        private Integer rating;
        private LocalDateTime createdAt;
    }

    /** GET /api/v1/members/me/likes, bookmarks — 매장 목록 공통 */
    @Getter
    @Builder
    public static class MyRestaurantResponse {
        private Long restaurantId;
        private String restaurantName;
        private String address;
        private String district;
        private String grade;
        private String category;
        private LocalDateTime createdAt;
    }
}
