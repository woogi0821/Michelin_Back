package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberSuspensionScheduler {

    private final MemberRepository memberRepository;

    /**
     * 매일 새벽 00:05 — 정지 기간 만료 회원 자동 복구
     */
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void releaseExpiredSuspensions() {
        LocalDate today = LocalDate.now();
        List<Member> expired = memberRepository.findExpiredSuspensions(today);

        if (expired.isEmpty()) return;

        expired.forEach(Member::releaseSuspension);
        log.info("[SuspensionScheduler] 정지 해제 처리: {}명 (기준일: {})", expired.size(), today);
    }
}
