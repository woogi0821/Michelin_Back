package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.dto.AdminDto;
import com.simplecoding.michelin_back.admin.dto.AdminLogDto;
import com.simplecoding.michelin_back.admin.repository.AdminRepository;
import com.simplecoding.michelin_back.admin.service.AdminLogService;
import com.simplecoding.michelin_back.admin.service.AdminService;
import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.CommonException;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('A','S')")
public class AdminController {

    private final AdminService adminService;
    private final AdminLogService adminLogService;
    private final AdminRepository adminRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminDto.Response>>> list() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAll()));
    }

    @PostMapping("/grant")
    public ResponseEntity<ApiResponse<AdminDto.Response>> grant(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody AdminDto.GrantRequest req) {
        Long adminId = getAdminId(user.getMemberId());
        return ResponseEntity.ok(ApiResponse.success(adminService.grant(adminId, req)));
    }

    @DeleteMapping("/{adminId}/revoke")
    public ResponseEntity<ApiResponse<Void>> revoke(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long adminId) {
        Long myAdminId = getAdminId(user.getMemberId());
        adminService.revoke(myAdminId, adminId);
        return ResponseEntity.ok(ApiResponse.success("관리자 권한이 회수되었습니다."));
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<Page<AdminLogDto.Response>>> logs(
            @PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminLogService.getAll(pageable)));
    }

    private Long getAdminId(Long memberId) {
        return adminRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> CommonException.forbidden("관리자 권한이 없습니다."))
                .getAdminId();
    }
}
