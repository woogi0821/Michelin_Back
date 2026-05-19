package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.dto.NoticeDto;
import com.simplecoding.michelin_back.admin.service.NoticeService;
import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.CustomUserDetails;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // ── 공개 ─────────────────────────────────────────────────
    @GetMapping("/notices")
    public ResponseEntity<ApiResponse<Page<NoticeDto.Response>>> list(
            @PageableDefault(size = 10, sort = "insertTime", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(noticeService.getList(pageable)));
    }

    @GetMapping("/notices/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeDto.Response>> detail(@PathVariable Long noticeId) {
        return ResponseEntity.ok(ApiResponse.success(noticeService.getOne(noticeId)));
    }

    // ── 관리자 전용 ────────────────────────────────────────────
    @PreAuthorize("hasAnyAuthority('A','S')")
    @PostMapping("/admin/notices")
    public ResponseEntity<ApiResponse<NoticeDto.Response>> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody NoticeDto.CreateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(noticeService.create(user.getMemberId(), req)));
    }

    @PreAuthorize("hasAnyAuthority('A','S')")
    @PutMapping("/admin/notices/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeDto.Response>> update(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long noticeId,
            @RequestBody NoticeDto.UpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(noticeService.update(user.getMemberId(), noticeId, req)));
    }

    @PreAuthorize("hasAnyAuthority('A','S')")
    @DeleteMapping("/admin/notices/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long noticeId) {
        noticeService.delete(user.getMemberId(), noticeId);
        return ResponseEntity.ok(ApiResponse.success("삭제되었습니다."));
    }
}
