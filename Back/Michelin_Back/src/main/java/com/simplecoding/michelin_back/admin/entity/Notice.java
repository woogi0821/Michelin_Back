package com.simplecoding.michelin_back.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 공지사항 엔티티
 * DB 컬럼: INSERT_TIME / UPDATE_TIME / DELET_TIME (BaseTimeEntity 미사용)
 * 소프트 딜리트 방식 — DELET_YN = 'Y' 처리
 * 한국어 상태에서만 노출 (외국어 상태 비노출은 프론트에서 처리)
 */
@Entity
@Table(name = "NOTICE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_notice")
    @SequenceGenerator(name = "seq_notice", sequenceName = "SEQ_NOTICE", allocationSize = 1)
    @Column(name = "NOTICE_ID")
    private Long noticeId;

    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITER_ID", nullable = false)
    private Admin writer;

    @Column(name = "FIX_YN", nullable = false, length = 1)
    private String fixYn = "N";

    @Column(name = "DELET_YN", nullable = false, length = 1)
    private String deletYn = "N";

    @CreatedDate
    @Column(name = "INSERT_TIME", updatable = false)
    private LocalDateTime insertTime;

    @LastModifiedDate
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    @Column(name = "DELET_TIME")
    private LocalDateTime deletTime;

    @Builder
    public Notice(String title, String content, Admin writer, String fixYn) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.fixYn = fixYn != null ? fixYn : "N";
        this.deletYn = "N";
    }

    // 수정
    public void update(String title, String content, String fixYn) {
        this.title = title;
        this.content = content;
        this.fixYn = fixYn;
    }

    // 소프트 딜리트
    public void delete() {
        this.deletYn = "Y";
        this.deletTime = LocalDateTime.now();
    }
}
