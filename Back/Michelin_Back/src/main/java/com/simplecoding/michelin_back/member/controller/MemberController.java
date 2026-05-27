package com.simplecoding.michelin_back.member.controller;

import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import com.simplecoding.michelin_back.member.dto.MemberPageDto;
import com.simplecoding.michelin_back.member.service.MemberPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberPageService memberPageService;

    /** 프로필 + 카운트 */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberPageDto.ProfileResponse>> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                memberPageService.getProfile(userDetails.getMemberId())));
    }

    /** 등급별 방문 현황 */
    @GetMapping("/me/michelin-stats")
    public ResponseEntity<ApiResponse<MemberPageDto.MichelinStatsResponse>> getMichelinStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                memberPageService.getMichelinStats(userDetails.getMemberId())));
    }

    /** 내 리뷰 목록 */
    @GetMapping("/me/reviews")
    public ResponseEntity<ApiResponse<List<MemberPageDto.MyReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                memberPageService.getMyReviews(userDetails.getMemberId())));
    }

    /** 좋아요한 매장 */
    @GetMapping("/me/likes")
    public ResponseEntity<ApiResponse<List<MemberPageDto.MyRestaurantResponse>>> getMyLikes(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                memberPageService.getMyLikes(userDetails.getMemberId())));
    }

    /** 북마크한 매장 */
    @GetMapping("/me/bookmarks")
    public ResponseEntity<ApiResponse<List<MemberPageDto.MyRestaurantResponse>>> getMyBookmarks(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                memberPageService.getMyBookmarks(userDetails.getMemberId())));
    }
}
