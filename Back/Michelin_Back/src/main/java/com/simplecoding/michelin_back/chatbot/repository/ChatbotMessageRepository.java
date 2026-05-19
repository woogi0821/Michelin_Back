package com.simplecoding.michelin_back.chatbot.repository;

import com.simplecoding.michelin_back.chatbot.entity.ChatbotMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatbotMessageRepository extends JpaRepository<ChatbotMessage, Long> {
    List<ChatbotMessage> findBySession_SessionIdOrderByInsertTimeAsc(Long sessionId);
    List<ChatbotMessage> findTop20BySession_SessionIdOrderByInsertTimeAsc(Long sessionId);
}
