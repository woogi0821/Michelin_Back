package com.simplecoding.michelin_back.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "INQUIRY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_inquiry")
    @SequenceGenerator(name = "seq_inquiry", sequenceName = "SEQ_INQUIRY", allocationSize = 1)
    @Column(name = "INQUIRY_ID")
    private Long inquiryId;

    @Column(name = "MEMBER_ID", nullable = false)
    private Long memberId;

    @Column(name = "ADMIN_ID")
    private Long adminId;

    @Column(name = "CATEGORY", length = 50)
    private String category;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Column(name = "CONTENT", length = 4000)
    private String content;

    @Builder.Default
    @Column(name = "STATUS", length = 20)
    private String status = "PENDING";

    @Column(name = "ANSWER", length = 4000)
    private String answer;

    @Column(name = "ANSWERED_AT")
    private LocalDateTime answeredAt;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "INSERT_TIME", updatable = false)
    private LocalDateTime insertTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    /** 관리자 답변 처리 */
    public void answer(Long adminId, String answerContent) {
        this.adminId = adminId;
        this.answer = answerContent;
        this.answeredAt = LocalDateTime.now();
        this.status = "ANSWERED";
    }
}
