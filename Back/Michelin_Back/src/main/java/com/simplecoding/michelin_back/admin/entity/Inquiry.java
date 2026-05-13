package com.simplecoding.michelin_back.admin.entity;

import com.simplecoding.michelin_back.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 고객센터 문의 엔티티
 * ADMIN_ID nullable — 문의 접수 시점엔 담당 관리자 미배정
 */
@Entity
@Table(name = "INQUIRY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_inquiry")
    @SequenceGenerator(name = "seq_inquiry", sequenceName = "SEQ_INQUIRY", allocationSize = 1)
    @Column(name = "INQUIRY_ID")
    private Long inquiryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_ID")
    private Admin admin;

    @Column(name = "CATEGORY", nullable = false, length = 20)
    private String category;

    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status = "PENDING";

    @Lob
    @Column(name = "ANSWER_CONTENT")
    private String answerContent;

    @Column(name = "ANSWER_AT")
    private LocalDateTime answerAt;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Inquiry(Member member, String category, String title) {
        this.member = member;
        this.category = category;
        this.title = title;
        this.status = "PENDING";
    }

    // 답변 등록
    public void answer(Admin admin, String answerContent) {
        this.admin = admin;
        this.answerContent = answerContent;
        this.answerAt = LocalDateTime.now();
        this.status = "ANSWERED";
    }
}
