package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.dto.AdminDto;
import com.simplecoding.michelin_back.admin.service.AdminService;
import com.simplecoding.michelin_back.admin.service.AdminManagementService;
import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminManagementService adminManagementService;

    // ── 대시보드 ──────────────────────────────────────────

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<AdminDto.DashboardStats>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboardStats()));
    }

    // ── 회원 관리 ──────────────────────────────────────────

    @GetMapping("/members")
    public ResponseEntity<ApiResponse<Page<AdminDto.MemberResponse>>> getMembers(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getMembers(keyword, pageable)));
    }

    /** 회원 정지 — PATCH */
    @PatchMapping("/members/{memberId}/suspend")
    public ResponseEntity<ApiResponse<Void>> suspendMember(
            @PathVariable Long memberId,
            @RequestBody AdminDto.SuspendRequest req) {
        adminService.suspendMember(memberId, req.getSuspendedUntil());
        return ResponseEntity.ok(ApiResponse.success("회원이 정지되었습니다."));
    }

    /** 회원 정지 해제 */
    @PostMapping("/members/{memberId}/release")
    public ResponseEntity<ApiResponse<Void>> releaseMember(@PathVariable Long memberId) {
        adminService.releaseMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("정지가 해제되었습니다."));
    }

    /** 회원 탈퇴 처리 — DELETE */
    @DeleteMapping("/members/{memberId}")
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

    /** 리뷰 삭제 */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        adminService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("리뷰가 삭제되었습니다."));
    }

    /** 리뷰 복구 — PATCH */
    @PatchMapping("/reviews/{reviewId}/restore")
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

    /** 문의 답변 — PATCH */
    @PatchMapping("/inquiries/{inquiryId}/answer")
    public ResponseEntity<ApiResponse<Void>> answerInquiry(
            @PathVariable Long inquiryId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AdminDto.AnswerRequest req) {
        adminService.answerInquiry(inquiryId, userDetails.getMemberId(), req.getAnswer());
        return ResponseEntity.ok(ApiResponse.success("답변이 등록되었습니다."));
    }

    // ── 레스토랑 관리 ──────────────────────────────────────────

    /** 레스토랑 목록 조회 (DELETED 포함) */
    @GetMapping("/restaurants")
    public ResponseEntity<ApiResponse<Page<AdminDto.RestaurantResponse>>> getRestaurants(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "") String status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getRestaurants(keyword, status, pageable)));
    }

    /** 레스토랑 삭제 */
    @DeleteMapping("/restaurants/{restaurantId}")
    public ResponseEntity<ApiResponse<Void>> deleteRestaurant(@PathVariable Long restaurantId) {
        adminService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.success("레스토랑이 삭제되었습니다."));
    }

    /** 레스토랑 복구 */
    @PatchMapping("/restaurants/{restaurantId}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreRestaurant(@PathVariable Long restaurantId) {
        adminService.restoreRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.success("레스토랑이 복구되었습니다."));
    }

    // ── 관리자 관리 ──────────────────────────────────────────

    /** 관리자 목록 조회 */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<AdminDto.AdminResponse>>> getAdminList() {
        return ResponseEntity.ok(ApiResponse.success(adminManagementService.getAdminList()));
    }

    /** 관리자 등록 */
    @PostMapping
    public ResponseEntity<ApiResponse<AdminDto.AdminResponse>> createAdmin(
            @RequestBody AdminDto.AdminCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(adminManagementService.createAdmin(req)));
    }

    /** 관리자 삭제 */
    @DeleteMapping("/{adminId}")
    public ResponseEntity<ApiResponse<Void>> deleteAdmin(@PathVariable Long adminId) {
        adminManagementService.deleteAdmin(adminId);
        return ResponseEntity.ok(ApiResponse.success("관리자가 삭제되었습니다."));
    }

    /** 관리자 역할 변경 */
    @PatchMapping("/{adminId}/role")
    public ResponseEntity<ApiResponse<Void>> updateAdminRole(
            @PathVariable Long adminId,
            @RequestParam String newRole) {
        adminManagementService.updateRole(adminId, newRole);
        return ResponseEntity.ok(ApiResponse.success("역할이 변경되었습니다."));
    }

    /** 관리자 담당 파트 변경 */
    @PatchMapping("/{adminId}/part")
    public ResponseEntity<ApiResponse<Void>> updateAdminPart(
            @PathVariable Long adminId,
            @RequestParam String newPart) {
        adminManagementService.updatePart(adminId, newPart);
        return ResponseEntity.ok(ApiResponse.success("담당 파트가 변경되었습니다."));
    }
}
