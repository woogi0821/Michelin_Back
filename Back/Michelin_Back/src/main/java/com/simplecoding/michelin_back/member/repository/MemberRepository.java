package com.simplecoding.michelin_back.member.repository;

import com.simplecoding.michelin_back.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // ✅ loginId로 멤버 조회 (JWT 인증용)
    Optional<Member> findByLoginId(String loginId);
}