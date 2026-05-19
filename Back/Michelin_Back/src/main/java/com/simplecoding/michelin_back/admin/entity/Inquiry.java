package com.simplecoding.michelin_back.admin.entity;

import com.simplecoding.michelin_back.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "INQUIRY")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_inquiry")
    @SequenceGenerator(name = "seq_inquiry", sequenceName = "SEQ_INQUIRY", allocationSize = 1)
    @Column(name = "INQUIRY_ID")
    private Long inquiryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Column(name = "CONTENT", nullable = false, length = 2000)
    private String content;

    /** PENDING / ANSWERED */
    @Builder.Default
    @Column(name = "STATUS", nullable = false, length = 10)
    private String status = "PENDING";

    @Column(name = "ANSWER", length = 2000)
    private String answer;

    @Column(name = "ANSWERED_AT")
    private LocalDateTime answeredAt;

    @CreatedDate
    @Column(name = "INSERT_TIME", updatable = false)
    private LocalDateTime insertTime;

    @LastModifiedDate
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    public void answer(String answer) {
        this.answer = answer;
        this.status = "ANSWERED";
        this.answeredAt = LocalDateTime.now();
    }
}
