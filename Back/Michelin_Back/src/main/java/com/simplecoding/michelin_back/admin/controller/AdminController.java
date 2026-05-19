package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.entity.Admin;
import com.simplecoding.michelin_back.admin.service.AdminService;
import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import com.simplecoding.michelin_back.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AdminController {

    private final AdminService adminService;

    // 현재 관리자 정보
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Admin>> getAdminInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Admin admin = adminService.getAdminInfo(userDetails);
        return ResponseEntity.ok(new ApiResponse<>(true, "관리자 정보 조회 성공", admin, 0, 1));
    }

    // 회원 목록 조회
    @GetMapping("/members")
    public ResponseEntity<ApiResponse<Page<Member>>> getMembers(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "insertTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Member> members = adminService.getMembers(status, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "회원 목록 조회 성공", members, (int) pageable.getOffset(), members.getTotalElements()));
    }

    // 회원 상세 조회
    @GetMapping("/members/{memberId}")
    public ResponseEntity<ApiResponse<Member>> getMember(@PathVariable Long memberId) {
        Member member = adminService.getMember(memberId);
        return ResponseEntity.ok(new ApiResponse<>(true, "회원 상세 조회 성공", member, 0, 1));
    }

    // 회원 정지
    @PatchMapping("/members/{memberId}/suspend")
    public ResponseEntity<ApiResponse<Void>> suspendMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate until) {
        adminService.suspendMember(userDetails, memberId, until);
        return ResponseEntity.ok(new ApiResponse<>(true, "회원 정지 처리 성공", null, 0, 0));
    }

    // 회원 정지 해제
    @PatchMapping("/members/{memberId}/release")
    public ResponseEntity<ApiResponse<Void>> releaseMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long memberId) {
        adminService.releaseMember(userDetails, memberId);
        return ResponseEntity.ok(new ApiResponse<>(true, "회원 정지 해제 성공", null, 0, 0));
    }
}
