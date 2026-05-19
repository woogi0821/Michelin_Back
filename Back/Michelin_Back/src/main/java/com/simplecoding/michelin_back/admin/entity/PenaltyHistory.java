package com.simplecoding.michelin_back.admin.entity;

import com.simplecoding.michelin_back.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 패널티 이력 (WARNING / SUSPEND)
 * 리뷰 신고 처리 또는 관리자 수동 패널티
 */
@Entity
@Table(name = "PENALTY_HISTORY")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class PenaltyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_penalty_history")
    @SequenceGenerator(name = "seq_penalty_history", sequenceName = "SEQ_PENALTY_HISTORY", allocationSize = 1)
    @Column(name = "PENALTY_ID")
    private Long penaltyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    /** 패널티 원인 리뷰 ID (nullable — 수동 패널티의 경우 null) */
    @Column(name = "REVIEW_ID")
    private Long reviewId;

    /** 처리한 관리자 ID (nullable — 자동 처리의 경우 null) */
    @Column(name = "ADMIN_ID")
    private Long adminId;

    @Column(name = "PENALTY_REASON", nullable = false, length = 500)
    private String penaltyReason;

    /** WARNING / SUSPEND */
    @Column(name = "PENALTY_TYPE", nullable = false, length = 10)
    private String penaltyType;

    /** 정지 일수 (SUSPEND 시에만 사용) */
    @Column(name = "SUSPEND_DAYS")
    private Integer suspendDays;

    @CreatedDate
    @Column(name = "INSERT_TIME", updatable = false)
    private LocalDateTime insertTime;
}
