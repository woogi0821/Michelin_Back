package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.dto.PenaltyHistoryDto;
import com.simplecoding.michelin_back.admin.entity.PenaltyHistory;
import com.simplecoding.michelin_back.admin.repository.PenaltyHistoryRepository;
import com.simplecoding.michelin_back.common.CommonException;
import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PenaltyService {

    private final PenaltyHistoryRepository penaltyHistoryRepository;
    private final MemberRepository memberRepository;
    private final AdminLogService adminLogService;

    /** 관리자 수동 패널티 부여 */
    @Transactional
    public PenaltyHistoryDto.Response givePenalty(Long adminId, PenaltyHistoryDto.CreateRequest req) {
        Member member = memberRepository.findById(req.getMemberId())
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));

        member.addPenalty();

        if ("SUSPEND".equals(req.getPenaltyType())) {
            int days = req.getSuspendDays() != null ? req.getSuspendDays() : 7;
            member.suspend(LocalDate.now().plusDays(days));
        }

        PenaltyHistory penalty = PenaltyHistory.builder()
                .member(member)
                .reviewId(req.getReviewId())
                .adminId(adminId)
                .penaltyReason(req.getPenaltyReason())
                .penaltyType(req.getPenaltyType())
                .suspendDays(req.getSuspendDays())
                .build();

        PenaltyHistory saved = penaltyHistoryRepository.save(penalty);

        adminLogService.log(adminId, "MEMBER_SUSPEND", req.getMemberId(),
                req.getPenaltyType() + " - " + req.getPenaltyReason());

        return toResponse(saved);
    }

    /** 정지 해제 */
    @Transactional
    public void releaseSuspension(Long memberId, Long adminId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));
        member.releaseSuspension();
        adminLogService.log(adminId, "MEMBER_SUSPEND_RELEASE", memberId, "정지 해제");
    }

    public List<PenaltyHistoryDto.Response> getMemberPenalties(Long memberId) {
        return penaltyHistoryRepository.findByMember_MemberIdOrderByInsertTimeDesc(memberId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Page<PenaltyHistoryDto.Response> getAll(Pageable pageable) {
        return penaltyHistoryRepository.findAllByOrderByInsertTimeDesc(pageable)
                .map(this::toResponse);
    }

    private PenaltyHistoryDto.Response toResponse(PenaltyHistory p) {
        return PenaltyHistoryDto.Response.builder()
                .penaltyId(p.getPenaltyId())
                .memberId(p.getMember().getMemberId())
                .memberName(p.getMember().getName())
                .reviewId(p.getReviewId())
                .adminId(p.getAdminId())
                .penaltyReason(p.getPenaltyReason())
                .penaltyType(p.getPenaltyType())
                .suspendDays(p.getSuspendDays())
                .insertTime(p.getInsertTime())
                .build();
    }
}
