package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.dto.InquiryDto;
import com.simplecoding.michelin_back.admin.service.InquiryService;
import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    // ── 로그인 회원 ────────────────────────────────────────────
    @PostMapping("/inquiries")
    public ResponseEntity<ApiResponse<InquiryDto.Response>> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody InquiryDto.CreateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(inquiryService.create(user.getMemberId(), req)));
    }

    @GetMapping("/inquiries/my")
    public ResponseEntity<ApiResponse<Page<InquiryDto.Response>>> myList(
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                inquiryService.getMyList(user.getMemberId(), pageable)));
    }

    // ── 관리자 전용 ────────────────────────────────────────────
    @PreAuthorize("hasAnyAuthority('A','S')")
    @GetMapping("/admin/inquiries")
    public ResponseEntity<ApiResponse<Page<InquiryDto.Response>>> adminList(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<InquiryDto.Response> result = (status != null)
                ? inquiryService.getByStatus(status, pageable)
                : inquiryService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PreAuthorize("hasAnyAuthority('A','S')")
    @PostMapping("/admin/inquiries/{inquiryId}/answer")
    public ResponseEntity<ApiResponse<InquiryDto.Response>> answer(
            @PathVariable Long inquiryId,
            @RequestBody InquiryDto.AnswerRequest req) {
        return ResponseEntity.ok(ApiResponse.success(inquiryService.answer(inquiryId, req)));
    }
}
