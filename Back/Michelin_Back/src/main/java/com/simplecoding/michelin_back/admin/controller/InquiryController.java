package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.dto.InquiryDto;
import com.simplecoding.michelin_back.admin.entity.Inquiry;
import com.simplecoding.michelin_back.admin.service.InquiryService;
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
@RequestMapping("/api/admin/inquiries")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class InquiryController {

    private final InquiryService inquiryService;

    // 문의 목록 (status: PENDING | ANSWERED | 전체)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Inquiry>>> getInquiries(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Inquiry> inquiries = inquiryService.getInquiries(status, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "문의 목록 조회 성공", inquiries, (int) pageable.getOffset(), inquiries.getTotalElements()));
    }

    // 문의 상세
    @GetMapping("/{inquiryId}")
    public ResponseEntity<ApiResponse<Inquiry>> getInquiry(@PathVariable Long inquiryId) {
        Inquiry inquiry = inquiryService.getInquiry(inquiryId);
        return ResponseEntity.ok(new ApiResponse<>(true, "문의 상세 조회 성공", inquiry, 0, 1));
    }

    // 문의 답변 등록
    @PostMapping("/{inquiryId}/answer")
    public ResponseEntity<ApiResponse<Inquiry>> answerInquiry(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long inquiryId,
            @Valid @RequestBody InquiryDto.AnswerRequest request) {
        Inquiry inquiry = inquiryService.answerInquiry(userDetails, inquiryId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "문의 답변 등록 성공", inquiry, 0, 1));
    }
}
