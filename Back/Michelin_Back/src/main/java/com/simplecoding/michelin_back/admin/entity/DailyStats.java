package com.simplecoding.michelin_back.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 일별 통계 (스케줄러가 매일 새벽 집계)
 */
@Entity
@Table(name = "DAILY_STATS")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class DailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_daily_stats")
    @SequenceGenerator(name = "seq_daily_stats", sequenceName = "SEQ_DAILY_STATS", allocationSize = 1)
    @Column(name = "STAT_ID")
    private Long statId;

    @Column(name = "STAT_DATE", nullable = false, unique = true)
    private LocalDate statDate;

    @Builder.Default
    @Column(name = "TOTAL_MEMBERS")
    private Long totalMembers = 0L;

    @Builder.Default
    @Column(name = "NEW_MEMBERS")
    private Long newMembers = 0L;

    @Builder.Default
    @Column(name = "ACTIVE_REVIEWS")
    private Long activeReviews = 0L;

    @Builder.Default
    @Column(name = "TOTAL_INQUIRIES")
    private Long totalInquiries = 0L;

    @Builder.Default
    @Column(name = "PENDING_INQUIRIES")
    private Long pendingInquiries = 0L;

    @CreatedDate
    @Column(name = "INSERT_TIME", updatable = false)
    private LocalDateTime insertTime;
}
