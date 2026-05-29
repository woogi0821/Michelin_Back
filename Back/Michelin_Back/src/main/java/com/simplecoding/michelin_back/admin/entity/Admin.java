package com.simplecoding.michelin_back.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ADMIN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_admin")
    @SequenceGenerator(name = "seq_admin", sequenceName = "SEQ_ADMIN", allocationSize = 1)
    @Column(name = "ADMIN_ID")
    private Long adminId;

    @Column(name = "MEMBER_ID", nullable = false)
    private Long memberId;

    /** MANAGER | SUPER */
    @Column(name = "ADMIN_ROLE", nullable = false, length = 20)
    private String adminRole;

    /** ALL | MEMBER | RESTAURANT | REVIEW | NOTICE | POPUP */
    @Column(name = "ADMIN_PART", nullable = false, length = 20)
    private String adminPart;

    @CreationTimestamp
    @Column(name = "INSERT_TIME", updatable = false)
    private LocalDateTime insertTime;

    @UpdateTimestamp
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    public void changeRole(String newRole) {
        this.adminRole = newRole;
    }

    public void changePart(String newPart) {
        this.adminPart = newPart;
    }
}
