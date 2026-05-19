package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.dto.PenaltyHistoryDto;
import com.simplecoding.michelin_back.admin.repository.AdminRepository;
import com.simplecoding.michelin_back.admin.service.PenaltyService;
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
public class PenaltyController {

    private final PenaltyService penaltyService;
    private final AdminRepository adminRepository;

    @PostMapping("/penalties")
    public ResponseEntity<ApiResponse<PenaltyHistoryDto.Response>> give(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody PenaltyHistoryDto.CreateRequest req) {
        Long adminId = getAdminId(user.getMemberId());
        return ResponseEntity.ok(ApiResponse.success(penaltyService.givePenalty(adminId, req)));
    }

    @DeleteMapping("/penalties/release/{memberId}")
    public ResponseEntity<ApiResponse<Void>> release(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long memberId) {
        Long adminId = getAdminId(user.getMemberId());
        penaltyService.releaseSuspension(memberId, adminId);
        return ResponseEntity.ok(ApiResponse.success("정지가 해제되었습니다."));
    }

    @GetMapping("/penalties/member/{memberId}")
    public ResponseEntity<ApiResponse<List<PenaltyHistoryDto.Response>>> memberPenalties(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(penaltyService.getMemberPenalties(memberId)));
    }

    @GetMapping("/penalties")
    public ResponseEntity<ApiResponse<Page<PenaltyHistoryDto.Response>>> all(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(penaltyService.getAll(pageable)));
    }

    private Long getAdminId(Long memberId) {
        return adminRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> CommonException.forbidden("관리자 권한이 없습니다."))
                .getAdminId();
    }
}
