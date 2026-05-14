package com.simplecoding.michelin_back.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantRequestDto {

    private String restaurantName;
    private String grade;
    private String city;
    private String district;
    private Double lat;
    private Double lng;
    private String isGreenStar;
    private String address;
    private String kakaoPlaceUrl;
    private String kakaoPlaceId;
    private String phone;
}