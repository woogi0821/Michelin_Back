package com.simplecoding.michelin_back.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESTAURANTS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String resNm;      // 레스토랑명

    private String city;       // 도시 (서울/부산)
    private String district;   // 지역구 (강남구/해운대구 등)
    private String address;       // 도로명/지번 상세 주소

    @Column(nullable = false)
    private String grade;      // 미쉐린 등급 (3스타, 빕 구르망 등)

    @Column(nullable = false)
    private Double lat;        // 위도

    @Column(nullable = false)
    private Double lng;        // 경도

    private String category;   // 업종 (한식, 양식 등 - 추후 확장용)

    private LocalDateTime updatedAt; // 데이터 업데이트 일시

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
