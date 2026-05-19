package com.simplecoding.michelin_back.chatbot.repository;

import com.simplecoding.michelin_back.chatbot.entity.ChatbotSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatbotSessionRepository extends JpaRepository<ChatbotSession, Long> {
    List<ChatbotSession> findByMember_MemberIdOrderByUpdateTimeDesc(Long memberId);
    Optional<ChatbotSession> findTopByMember_MemberIdOrderByUpdateTimeDesc(Long memberId);
}
