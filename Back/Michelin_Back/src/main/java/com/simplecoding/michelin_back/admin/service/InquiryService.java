package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.dto.InquiryDto;
import com.simplecoding.michelin_back.admin.entity.Admin;
import com.simplecoding.michelin_back.admin.entity.Inquiry;
import com.simplecoding.michelin_back.admin.repository.AdminRepository;
import com.simplecoding.michelin_back.admin.repository.InquiryRepository;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final AdminRepository adminRepository;
    private final AdminLogService adminLogService;

    // 문의 목록 (상태 필터, null이면 전체)
    @Transactional(readOnly = true)
    public Page<Inquiry> getInquiries(String status, Pageable pageable) {
        if (status == null || status.isBlank()) {
            return inquiryRepository.findAll(pageable);
        }
        return inquiryRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    // 문의 상세
    @Transactional(readOnly = true)
    public Inquiry getInquiry(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 문의입니다."));
    }

    // 문의 답변 등록
    @Transactional
    public Inquiry answerInquiry(CustomUserDetails userDetails, Long inquiryId, InquiryDto.AnswerRequest request) {
        Admin admin = getAdmin(userDetails.getMemberId());
        Inquiry inquiry = getInquiry(inquiryId);

        if ("ANSWERED".equals(inquiry.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 답변이 완료된 문의입니다.");
        }

        inquiry.answer(admin, request.getAnswerContent());
        adminLogService.log(admin, "INQUIRY_ANSWER", "INQUIRY", inquiryId, inquiry.getTitle());
        return inquiry;
    }

    private Admin getAdmin(Long memberId) {
        return adminRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 없습니다."));
    }
}
