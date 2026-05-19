package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.dto.PenaltyHistoryDto;
import com.simplecoding.michelin_back.admin.entity.Admin;
import com.simplecoding.michelin_back.admin.entity.PenaltyHistory;
import com.simplecoding.michelin_back.admin.repository.AdminRepository;
import com.simplecoding.michelin_back.admin.repository.PenaltyHistoryRepository;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyHistoryRepository penaltyHistoryRepository;
    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;

    // 특정 회원 패널티 내역
    @Transactional(readOnly = true)
    public Page<PenaltyHistory> getPenalties(Long memberId, Pageable pageable) {
        return penaltyHistoryRepository.findByMember_MemberIdOrderByCreatedAtDesc(memberId, pageable);
    }

    // 패널티 부여
    @Transactional
    public PenaltyHistory givePenalty(CustomUserDetails userDetails, PenaltyHistoryDto.Request request) {
        Admin admin = getAdmin(userDetails.getMemberId());
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));

        PenaltyHistory penalty = PenaltyHistory.builder()
                .member(member)
                .admin(admin)
                .reason(request.getReason())
                .build();

        member.addPenalty();
        return penaltyHistoryRepository.save(penalty);
    }

    // 패널티 취소
    @Transactional
    public void revokePenalty(CustomUserDetails userDetails, Long penaltyId) {
        getAdmin(userDetails.getMemberId());
        PenaltyHistory penalty = penaltyHistoryRepository.findById(penaltyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 패널티입니다."));

        if ("REVOKED".equals(penalty.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 취소된 패널티입니다.");
        }

        penalty.revoke();
    }

    private Admin getAdmin(Long memberId) {
        return adminRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 없습니다."));
    }
}
