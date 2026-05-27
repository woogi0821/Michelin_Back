package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.dto.AdminDto;
import com.simplecoding.michelin_back.admin.service.AdminService;
import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ── 대시보드 ──────────────────────────────────────────

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<AdminDto.DashboardStats>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboardStats()));
    }

    // ── 회원 관리 ──────────────────────────────────────────

    @GetMapping("/members")
    public ResponseEntity<ApiResponse<Page<AdminDto.MemberResponse>>> getMembers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getMembers(pageable)));
    }

    @PostMapping("/members/{memberId}/suspend")
    public ResponseEntity<ApiResponse<Void>> suspendMember(
            @PathVariable Long memberId,
            @RequestBody AdminDto.SuspendRequest req) {
        adminService.suspendMember(memberId, req.getSuspendedUntil());
        return ResponseEntity.ok(ApiResponse.success("회원이 정지되었습니다."));
    }

    @PostMapping("/members/{memberId}/release")
    public ResponseEntity<ApiResponse<Void>> releaseMember(@PathVariable Long memberId) {
        adminService.releaseMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("정지가 해제되었습니다."));
    }

    @PostMapping("/members/{memberId}/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawMember(@PathVariable Long memberId) {
        adminService.withdrawMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("회원이 탈퇴 처리되었습니다."));
    }

    // ── 리뷰 관리 ──────────────────────────────────────────

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<Page<AdminDto.ReviewResponse>>> getReviews(
            @RequestParam(defaultValue = "ALL") String status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getReviews(status, pageable)));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        adminService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("리뷰가 삭제되었습니다."));
    }

    @PostMapping("/reviews/{reviewId}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreReview(@PathVariable Long reviewId) {
        adminService.restoreReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("리뷰가 복구되었습니다."));
    }

    @PostMapping("/reviews/{reviewId}/blind")
    public ResponseEntity<ApiResponse<Void>> blindReview(@PathVariable Long reviewId) {
        adminService.blindReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("리뷰가 블라인드 처리되었습니다."));
    }

    // ── 문의 관리 ──────────────────────────────────────────

    @GetMapping("/inquiries")
    public ResponseEntity<ApiResponse<Page<AdminDto.InquiryResponse>>> getInquiries(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getInquiries(status, pageable)));
    }

    @PostMapping("/inquiries/{inquiryId}/answer")
    public ResponseEntity<ApiResponse<Void>> answerInquiry(
            @PathVariable Long inquiryId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AdminDto.AnswerRequest req) {
        adminService.answerInquiry(inquiryId, userDetails.getMemberId(), req.getAnswer());
        return ResponseEntity.ok(ApiResponse.success("답변이 등록되었습니다."));
    }
}
