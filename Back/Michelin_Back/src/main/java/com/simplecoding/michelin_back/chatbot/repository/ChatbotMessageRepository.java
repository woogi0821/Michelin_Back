package com.simplecoding.michelin_back.chatbot.repository;

import com.simplecoding.michelin_back.chatbot.entity.ChatbotMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatbotMessageRepository extends JpaRepository<ChatbotMessage, Long> {

    // 세션의 전체 대화 이력 (시간순)
    List<ChatbotMessage> findBySession_SessionIdOrderByCreatedAtAsc(Long sessionId);
}
