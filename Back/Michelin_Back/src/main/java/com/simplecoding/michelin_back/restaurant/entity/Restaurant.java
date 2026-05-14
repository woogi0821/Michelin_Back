package com.simplecoding.michelin_back.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String restaurantName;      // 레스토랑명

    private String city;       // 도시 (서울/부산)
    private String district;   // 지역구 (강남구/해운대구 등)
    private String address; // 도로명/지번 상세 주소
    private String phone;

    @Column(nullable = false)
    private String grade;      // 미쉐린 등급 (3스타, 빕 구르망 등)

    @Column(nullable = false)
    private Double lat;        // 위도

    @Column(nullable = false)
    private Double lng;        // 경도

    @Column(name = "CATEGORY") // DB 컬럼과 연결
    private String category;
    public String getCategory() {
        return category;
    }

//    private String category;   // 업종 (한식, 양식 등 - 추후 확장용)
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt; // 데이터 업데이트 일시

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RestaurantImage> images = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
