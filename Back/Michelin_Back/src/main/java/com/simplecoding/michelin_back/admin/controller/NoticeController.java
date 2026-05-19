package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.dto.NoticeDto;
import com.simplecoding.michelin_back.admin.entity.Notice;
import com.simplecoding.michelin_back.admin.service.NoticeService;
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
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 목록 (일반 사용자도 조회 가능)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Notice>>> getNotices(
            @PageableDefault(size = 10, sort = "insertTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Notice> notices = noticeService.getNotices(pageable);
        ApiResponse<Page<Notice>> response = new ApiResponse<>(true, "공지사항 목록 조회 성공", notices, (int) pageable.getOffset(), notices.getTotalElements());
        return ResponseEntity.ok(response);
    }

    // 공지사항 상세
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Notice>> getNotice(@PathVariable Long noticeId) {
        Notice notice = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "공지사항 상세 조회 성공", notice, 0, 1));
    }

    // 공지사항 등록 (관리자 전용)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Notice>> createNotice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody NoticeDto.Request request) {
        Notice notice = noticeService.createNotice(userDetails, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "공지사항 등록 성공", notice, 0, 1));
    }

    // 공지사항 수정 (관리자 전용)
    @PutMapping("/{noticeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Notice>> updateNotice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeDto.Request request) {
        Notice notice = noticeService.updateNotice(userDetails, noticeId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "공지사항 수정 성공", notice, 0, 1));
    }

    // 공지사항 삭제 (관리자 전용)
    @DeleteMapping("/{noticeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long noticeId) {
        noticeService.deleteNotice(userDetails, noticeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "공지사항 삭제 성공", null, 0, 0));
    }
}
