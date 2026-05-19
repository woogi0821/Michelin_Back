package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.dto.PenaltyHistoryDto;
import com.simplecoding.michelin_back.admin.entity.PenaltyHistory;
import com.simplecoding.michelin_back.admin.service.PenaltyService;
import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/penalties")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class PenaltyController {

    private final PenaltyService penaltyService;

    // 특정 회원 패널티 내역
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<Page<PenaltyHistory>>> getPenalties(
            @PathVariable Long memberId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PenaltyHistory> penalties = penaltyService.getPenalties(memberId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "패널티 내역 조회 성공", penalties, (int) pageable.getOffset(), penalties.getTotalElements()));
    }

    // 패널티 부여
    @PostMapping
    public ResponseEntity<ApiResponse<PenaltyHistory>> givePenalty(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PenaltyHistoryDto.Request request) {
        PenaltyHistory penalty = penaltyService.givePenalty(userDetails, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "패널티 부여 성공", penalty, 0, 1));
    }

    // 패널티 취소
    @PatchMapping("/{penaltyId}/revoke")
    public ResponseEntity<ApiResponse<Void>> revokePenalty(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long penaltyId) {
        penaltyService.revokePenalty(userDetails, penaltyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "패널티 취소 성공", null, 0, 0));
    }
}
