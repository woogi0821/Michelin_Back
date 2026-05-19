package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.PenaltyHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PenaltyHistoryRepository extends JpaRepository<PenaltyHistory, Long> {

    // 특정 회원의 패널티 내역
    Page<PenaltyHistory> findByMember_MemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    // 특정 회원의 현재 유효한 패널티 (APPLIED 상태 최신 1건)
    Optional<PenaltyHistory> findTopByMember_MemberIdAndStatusOrderByCreatedAtDesc(Long memberId, String status);

    // 특정 기간 패널티 부여 건수 (일별 통계 집계용)
    long countByStatusAndCreatedAtBetween(String status, java.time.LocalDateTime from, java.time.LocalDateTime to);
}
