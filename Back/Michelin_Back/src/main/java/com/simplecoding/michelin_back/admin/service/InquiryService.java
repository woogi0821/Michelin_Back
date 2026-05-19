package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.dto.InquiryDto;
import com.simplecoding.michelin_back.admin.entity.Inquiry;
import com.simplecoding.michelin_back.admin.repository.InquiryRepository;
import com.simplecoding.michelin_back.common.CommonException;
import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    /** 회원 문의 등록 */
    @Transactional
    public InquiryDto.Response create(Long memberId, InquiryDto.CreateRequest req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));

        Inquiry inquiry = Inquiry.builder()
                .member(member)
                .title(req.getTitle())
                .content(req.getContent())
                .build();
        return toResponse(inquiryRepository.save(inquiry));
    }

    /** 내 문의 목록 */
    public Page<InquiryDto.Response> getMyList(Long memberId, Pageable pageable) {
        return inquiryRepository.findByMember_MemberIdOrderByInsertTimeDesc(memberId, pageable)
                .map(this::toResponse);
    }

    /** 관리자 — 전체 문의 목록 */
    public Page<InquiryDto.Response> getAll(Pageable pageable) {
        return inquiryRepository.findAllByOrderByInsertTimeDesc(pageable)
                .map(this::toResponse);
    }

    /** 관리자 — 상태별 필터 */
    public Page<InquiryDto.Response> getByStatus(String status, Pageable pageable) {
        return inquiryRepository.findByStatusOrderByInsertTimeDesc(status, pageable)
                .map(this::toResponse);
    }

    /** 관리자 — 답변 등록 */
    @Transactional
    public InquiryDto.Response answer(Long inquiryId, InquiryDto.AnswerRequest req) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> CommonException.notFound("문의를 찾을 수 없습니다."));
        inquiry.answer(req.getAnswer());
        return toResponse(inquiry);
    }

    private InquiryDto.Response toResponse(Inquiry i) {
        return InquiryDto.Response.builder()
                .inquiryId(i.getInquiryId())
                .memberId(i.getMember().getMemberId())
                .memberName(i.getMember().getName())
                .title(i.getTitle())
                .content(i.getContent())
                .status(i.getStatus())
                .answer(i.getAnswer())
                .answeredAt(i.getAnsweredAt())
                .insertTime(i.getInsertTime())
                .build();
    }
}
