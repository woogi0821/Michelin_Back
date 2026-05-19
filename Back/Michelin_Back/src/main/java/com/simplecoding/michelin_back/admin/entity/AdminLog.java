package com.simplecoding.michelin_back.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 관리자 행동 로그 (REVIEW_DELETE, NOTICE_CREATE, INQUIRY_ANSWER 등)
 * 패널티 관련 로그는 PENALTY_HISTORY 별도 관리
 */
@Entity
@Table(name = "ADMIN_LOG")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_admin_log")
    @SequenceGenerator(name = "seq_admin_log", sequenceName = "SEQ_ADMIN_LOG", allocationSize = 1)
    @Column(name = "ADMIN_LOG_ID")
    private Long adminLogId;

    @Column(name = "ADMIN_ID", nullable = false)
    private Long adminId;

    /** 조치 대상 엔티티 ID (리뷰ID, 공지ID 등) */
    @Column(name = "TARGET_ID")
    private Long targetId;

    /** 행동 유형: REVIEW_DELETE / NOTICE_CREATE / NOTICE_UPDATE / INQUIRY_ANSWER / MEMBER_GRADE_CHANGE */
    @Column(name = "ADMIN_ACTION", nullable = false, length = 30)
    private String adminAction;

    /** 상세 설명 */
    @Column(name = "ACTION_DETAIL", length = 500)
    private String actionDetail;

    @CreatedDate
    @Column(name = "INSERT_TIME", updatable = false)
    private LocalDateTime insertTime;
}
