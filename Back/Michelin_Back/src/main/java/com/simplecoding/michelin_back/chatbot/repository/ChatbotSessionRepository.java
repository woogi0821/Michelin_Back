package com.simplecoding.michelin_back.chatbot.repository;

import com.simplecoding.michelin_back.chatbot.entity.ChatbotSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatbotSessionRepository extends JpaRepository<ChatbotSession, Long> {

    // 특정 회원의 가장 최근 세션 조회
    Optional<ChatbotSession> findTopByMember_MemberIdOrderByCreatedAtDesc(Long memberId);

    // 일별 통계 집계용
    long countByCreatedAtBetween(java.time.LocalDateTime from, java.time.LocalDateTime to);
}
