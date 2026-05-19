package com.simplecoding.michelin_back.chatbot.entity;

import com.simplecoding.michelin_back.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CHATBOT_SESSION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatbotSession {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_chatbot_session")
    @SequenceGenerator(name = "seq_chatbot_session", sequenceName = "SEQ_CHATBOT_SESSION", allocationSize = 1)
    @Column(name = "SESSION_ID")
    private Long sessionId;

    // 비회원 사용 차단 목적이므로 nullable = false로 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public ChatbotSession(Member member) {
        this.member = member;
    }
}
