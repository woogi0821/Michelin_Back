package com.simplecoding.michelin_back.chatbot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "CHATBOT_MESSAGE")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class ChatbotMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_chatbot_message")
    @SequenceGenerator(name = "seq_chatbot_message", sequenceName = "SEQ_CHATBOT_MESSAGE", allocationSize = 1)
    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SESSION_ID", nullable = false)
    private ChatbotSession session;

    /** USER / ASSISTANT */
    @Column(name = "ROLE", nullable = false, length = 10)
    private String role;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @CreatedDate
    @Column(name = "INSERT_TIME", updatable = false)
    private LocalDateTime insertTime;
}
