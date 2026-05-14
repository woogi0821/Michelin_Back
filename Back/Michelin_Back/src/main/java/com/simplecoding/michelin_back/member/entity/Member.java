package com.simplecoding.michelin_back.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER") // DB 테이블명과 똑같이!
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    private Long memberId; // PK

    @Column(name = "NAME")
    private String name;

    @Column(name = "LOGIN_ID")
    private String loginId;
}