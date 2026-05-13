package com.simplecoding.michelin_back.admin.entity;

import com.simplecoding.michelin_back.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

/**
 * 관리자 엔티티
 * ADMIN_ROLE: SUPER_ADMIN (전체 권한) / CONTENT_ADMIN (콘텐츠 관리 권한)
 */
@Entity
@Table(name = "ADMIN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_admin")
    @SequenceGenerator(name = "seq_admin", sequenceName = "SEQ_ADMIN", allocationSize = 1)
    @Column(name = "ADMIN_ID")
    private Long adminId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Column(name = "ADMIN_ROLE", length = 20)
    private String adminRole;

    @Builder
    public Admin(Member member, String adminRole) {
        this.member = member;
        this.adminRole = adminRole;
    }
}
