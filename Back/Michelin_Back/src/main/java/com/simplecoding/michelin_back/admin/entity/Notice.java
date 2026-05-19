package com.simplecoding.michelin_back.admin.entity;

import com.simplecoding.michelin_back.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 공지사항 엔티티
 * BaseTimeEntity 상속 — insertTime(INSERT_TIME) / updateTime(UPDATE_TIME)
 * 소프트 딜리트 방식 — DELET_YN = 'Y' 처리, DELET_TIME 별도 관리
 * 한국어 상태에서만 노출 (외국어 비노출은 프론트에서 처리)
 */
@Entity
@Table(name = "NOTICE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseTimeEntity {

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

    public void update(String title, String content, String fixYn) {
        this.title = title;
        this.content = content;
        this.fixYn = fixYn;
    }

    public void delete() {
        this.deletYn = "Y";
        this.deletTime = LocalDateTime.now();
    }
}
