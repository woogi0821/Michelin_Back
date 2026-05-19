package com.simplecoding.michelin_back.member.repository;

import com.simplecoding.michelin_back.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByEmail(String email);

    // 관리자 회원 목록 조회 (상태 필터)
    Page<Member> findByStatusOrderByInsertTimeDesc(String status, Pageable pageable);

    // 일별 통계 집계용
    long countByInsertTimeBetween(LocalDateTime from, LocalDateTime to);
}
