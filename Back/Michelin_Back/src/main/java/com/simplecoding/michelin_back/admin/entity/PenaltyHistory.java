package com.simplecoding.michelin_back.admin.entity;

import com.simplecoding.michelin_back.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 패널티 내역 엔티티
 * ADMIN_LOG와 역할 분리 — 패널티 도메인 전담
 * STATUS: APPLIED(부여) / REVOKED(취소)
 */
@Entity
@Table(name = "PENALTY_HISTORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PenaltyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_penalty")
    @SequenceGenerator(name = "seq_penalty", sequenceName = "SEQ_PENALTY_HISTORY", allocationSize = 1)
    @Column(name = "PENALTY_ID")
    private Long penaltyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_ID", nullable = false)
    private Admin admin;

    @Column(name = "REASON", nullable = false, length = 255)
    private String reason;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status = "APPLIED";

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public PenaltyHistory(Member member, Admin admin, String reason) {
        this.member = member;
        this.admin = admin;
        this.reason = reason;
        this.status = "APPLIED";
    }

    // 패널티 취소
    public void revoke() {
        this.status = "REVOKED";
    }
}
