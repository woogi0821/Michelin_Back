package com.simplecoding.michelin_back.restaurant.entity;

import com.simplecoding.michelin_back.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "RESTAURANTS")
public class Restaurant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurant_seq")
    @SequenceGenerator(name = "restaurant_seq", sequenceName = "RESTAURANTS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "RESTAURANT_NAME", nullable = false, length = 255)
    private String restaurantName;

    @Column(name = "GRADE", nullable = false, length = 100)
    private String grade;

    @Column(name = "CITY", length = 100)
    private String city;

    @Column(name = "DISTRICT", length = 100)
    private String district;

    @Column(name = "LAT", nullable = false)
    private Double lat;

    @Column(name = "LNG", nullable = false)
    private Double lng;

    @Column(name = "IS_GREEN_STAR", length = 10)
    private String isGreenStar;

    @Column(name = "ADDRESS", length = 500)
    private String address;

    @Column(name = "KAKAO_PLACE_URL", length = 500)
    private String kakaoPlaceUrl;

    @Column(name = "KAKAO_PLACE_ID", length = 100)
    private String kakaoPlaceId;

    @Column(name = "PHONE", length = 50)
    private String phone;

    @Column(name = "VIEW_COUNT")
    private Long viewCount = 0L;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantImage> images = new ArrayList<>();

    @Builder
    public Restaurant(String restaurantName, String grade, String city,
                      String district, Double lat, Double lng,
                      String isGreenStar, String address,
                      String kakaoPlaceUrl, String kakaoPlaceId, String phone) {
        this.restaurantName = restaurantName;
        this.grade = grade;
        this.city = city;
        this.district = district;
        this.lat = lat;
        this.lng = lng;
        this.isGreenStar = isGreenStar;
        this.address = address;
        this.kakaoPlaceUrl = kakaoPlaceUrl;
        this.kakaoPlaceId = kakaoPlaceId;
        this.phone = phone;
        this.viewCount = 0L;
    }

    // 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 음식점 수정
    public void update(String restaurantName, String grade, String city,
                       String district, String address, String phone,
                       String isGreenStar, String kakaoPlaceUrl, String kakaoPlaceId) {
        this.restaurantName = restaurantName;
        this.grade = grade;
        this.city = city;
        this.district = district;
        this.address = address;
        this.phone = phone;
        this.isGreenStar = isGreenStar;
        this.kakaoPlaceUrl = kakaoPlaceUrl;
        this.kakaoPlaceId = kakaoPlaceId;
    }
}