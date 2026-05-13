package com.simplecoding.michelin_back.member.entity;

import com.simplecoding.michelin_back.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 회원 엔티티
 * ※ 회원 담당 파트에서 필요한 필드 추가 예정
 */
@Entity
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_member")
    @SequenceGenerator(name = "seq_member", sequenceName = "SEQ_MEMBER", allocationSize = 1)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "LOGIN_ID", nullable = false, length = 50)
    private String loginId;

    @Column(name = "LOGIN_PW", nullable = false, length = 255)
    private String loginPw;

    @Column(name = "EMAIL", nullable = false, length = 255)
    private String email;

    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "PHONE", nullable = false, length = 20)
    private String phone;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "MEMBER_GRADE", nullable = false, length = 2)
    private String memberGrade = "N";

    @Column(name = "PROVIDER", length = 20)
    private String provider = "LOCAL";

    @Column(name = "PROVIDER_ID", length = 100)
    private String providerId;

    @Column(name = "PENALTY_COUNT")
    private Integer penaltyCount = 0;

    @Column(name = "SUSPENDED_UNTIL")
    private LocalDate suspendedUntil;
}
