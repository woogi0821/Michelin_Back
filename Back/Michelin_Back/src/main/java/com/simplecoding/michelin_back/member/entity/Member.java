package com.simplecoding.michelin_back.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq")
    @SequenceGenerator(name = "member_seq", sequenceName = "MEMBER_SEQ", allocationSize = 1)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "LOGIN_ID", length = 50)
    private String loginId;

    @Column(name = "LOGIN_PW", length = 255)
    private String loginPw;

    @Column(name = "EMAIL", length = 255)
    private String email;

    @Column(name = "NAME", length = 50)
    private String name;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "STATUS", length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "MEMBER_GRADE", length = 2)
    @Builder.Default
    private String memberGrade = "N";

    @Column(name = "PROVIDER", length = 20)
    @Builder.Default
    private String provider = "LOCAL";

    @Column(name = "PROVIDER_ID", length = 100)
    private String providerId;

    @Column(name = "PENALTY_COUNT")
    @Builder.Default
    private Integer penaltyCount = 0;

    @Column(name = "SUSPENDED_UNTIL")
    private LocalDateTime suspendedUntil;

    @Column(name = "INSERT_TIME")
    private LocalDateTime insertTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    @PrePersist
    public void onCreate() {
        this.insertTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    // ✅ 계정 정지 처리
    public void suspend(LocalDateTime until) {
        this.status = "SUSPENDED";
        this.suspendedUntil = until;
    }

    // ✅ 계정 활성화
    public void activate() {
        this.status = "ACTIVE";
        this.suspendedUntil = null;
    }
}