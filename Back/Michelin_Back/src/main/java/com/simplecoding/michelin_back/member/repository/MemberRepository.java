package com.simplecoding.michelin_back.member.repository;

import com.simplecoding.michelin_back.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByEmail(String email);
    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);

    /** 소셜 로그인 — provider + providerId로 회원 조회 */
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

    /** 어드민 회원 검색 — 이름/이메일/아이디 LIKE */
    @Query("SELECT m FROM Member m WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(m.loginId) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Member> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /** 정지 만료된 회원 목록 (스케줄러용) */
    @Query("SELECT m FROM Member m WHERE m.status = 'SUSPENDED' AND m.suspendedUntil <= :today")
    List<Member> findExpiredSuspensions(@Param("today") LocalDate today);
}
