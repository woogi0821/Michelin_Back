package com.simplecoding.michelin_back.social.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESTAURANT_BOOKMARK",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "restaurant_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RestaurantBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    // 나중에 폴더별로 맛집을 분류하고 싶을 때를 대비한 필드입니다.
    @Column(name = "folder_name")
    private String folderName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        // 폴더명이 지정되지 않았을 경우 기본값 설정
        if (this.folderName == null) {
            this.folderName = "기본 폴더";
        }
    }
}
