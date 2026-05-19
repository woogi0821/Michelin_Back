package com.simplecoding.michelin_back.admin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 관리자 활동 로그 엔티티
 * 패널티 관련 액션은 PenaltyHistory 전담 (역할 분리)
 * ACTION 예시: REVIEW_DELETE, NOTICE_CREATE, INQUIRY_ANSWER, RESTAURANT_UPDATE
 * TARGET_TYPE 예시: REVIEW, NOTICE, INQUIRY, RESTAURANT
 */
@Entity
@Table(name = "ADMIN_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_admin_log")
    @SequenceGenerator(name = "seq_admin_log", sequenceName = "SEQ_ADMIN_LOG", allocationSize = 1)
    @Column(name = "LOG_ID")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_ID", nullable = false)
    private Admin admin;

    @Column(name = "ACTION", nullable = false, length = 50)
    private String action;

    @Column(name = "TARGET_TYPE", length = 30)
    private String targetType;

    @Column(name = "TARGET_ID")
    private Long targetId;

    @Column(name = "DETAIL", length = 500)
    private String detail;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public AdminLog(Admin admin, String action, String targetType, Long targetId, String detail) {
        this.admin = admin;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.detail = detail;
    }
}
