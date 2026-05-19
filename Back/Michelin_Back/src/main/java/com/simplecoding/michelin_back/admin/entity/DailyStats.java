package com.simplecoding.michelin_back.admin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 일별 통계 엔티티
 * Scheduler로 매일 새벽 집계 — 대시보드 실시간 쿼리 부하 분산
 * STATS_DATE UNIQUE — 하루 1건만 허용
 * NEW_RESTAURANTS 제외 — 미슐랭 특성상 음식점 변동 빈도 낮음
 */
@Entity
@Table(name = "DAILY_STATS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_daily_stats")
    @SequenceGenerator(name = "seq_daily_stats", sequenceName = "SEQ_DAILY_STATS", allocationSize = 1)
    @Column(name = "STATS_ID")
    private Long statsId;

    @Column(name = "STATS_DATE", nullable = false, unique = true)
    private LocalDate statsDate;

    @Column(name = "NEW_MEMBERS")
    private Long newMembers = 0L;

    @Column(name = "NEW_REVIEWS")
    private Long newReviews = 0L;

    @Column(name = "NEW_PENALTIES")
    private Long newPenalties = 0L;

    @Column(name = "TOTAL_INQUIRIES")
    private Long totalInquiries = 0L;

    @Column(name = "ANSWERED_INQUIRIES")
    private Long answeredInquiries = 0L;

    @Column(name = "CHATBOT_SESSIONS")
    private Long chatbotSessions = 0L;

    @Column(name = "TOTAL_TOKENS")
    private Long totalTokens = 0L;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public DailyStats(LocalDate statsDate, Long newMembers, Long newReviews, Long newPenalties,
                      Long totalInquiries, Long answeredInquiries, Long chatbotSessions, Long totalTokens) {
        this.statsDate = statsDate;
        this.newMembers = newMembers;
        this.newReviews = newReviews;
        this.newPenalties = newPenalties;
        this.totalInquiries = totalInquiries;
        this.answeredInquiries = answeredInquiries;
        this.chatbotSessions = chatbotSessions;
        this.totalTokens = totalTokens;
    }
}
