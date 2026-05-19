package com.simplecoding.michelin_back.member.repository;

import com.simplecoding.michelin_back.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}