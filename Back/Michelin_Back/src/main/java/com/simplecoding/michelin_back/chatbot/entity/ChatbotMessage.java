package com.simplecoding.michelin_back.chatbot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CHATBOT_MESSAGE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatbotMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_chatbot_message")
    @SequenceGenerator(name = "seq_chatbot_message", sequenceName = "SEQ_CHATBOT_MESSAGE", allocationSize = 1)
    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SESSION_ID", nullable = false)
    private ChatbotSession session;

    // USER | ASSISTANT
    @Column(name = "ROLE", nullable = false, length = 10)
    private String role;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    // 토큰 수 (Python 응답에 포함 시 저장, 현재는 0)
    @Column(name = "TOKEN_USED")
    private Long tokenUsed;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public ChatbotMessage(ChatbotSession session, String role, String content, Long tokenUsed) {
        this.session = session;
        this.role = role;
        this.content = content;
        this.tokenUsed = tokenUsed != null ? tokenUsed : 0L;
    }
}
