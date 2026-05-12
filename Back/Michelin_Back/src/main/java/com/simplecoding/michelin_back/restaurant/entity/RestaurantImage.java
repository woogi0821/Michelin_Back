package com.simplecoding.michelin_back.restaurant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

@Entity
@Table(name = "RESTAURANT_IMAGES")
@Getter
@NoArgsConstructor
public class RestaurantImage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IMAGE_SEQ")
    @SequenceGenerator(name = "IMAGE_SEQ", sequenceName = "SEQ_IMAGE_ID", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant restaurant; // 부모 식당과 연결

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "IS_MAIN")
    private String isMain; // 'Y'면 메인 사진
}
