package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 전체 문의 목록 (관리자용 — 상태 필터)
    Page<Inquiry> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    // 특정 회원의 문의 목록
    Page<Inquiry> findByMember_MemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    // 미처리 문의 건수 (대시보드용)
    long countByStatus(String status);

    // 일별 통계 집계용
    long countByCreatedAtBetween(java.time.LocalDateTime from, java.time.LocalDateTime to);
    long countByStatusAndCreatedAtBetween(String status, java.time.LocalDateTime from, java.time.LocalDateTime to);
}
