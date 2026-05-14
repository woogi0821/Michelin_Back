package com.simplecoding.michelin_back.restaurant.service;

import com.simplecoding.michelin_back.common.MarkerDto;
import com.simplecoding.michelin_back.restaurant.entity.Restaurant;
import com.simplecoding.michelin_back.restaurant.entity.RestaurantImage;
import com.simplecoding.michelin_back.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kotlin.reflect.jvm.internal.impl.builtins.StandardNames.FqNames.list;

// QRestaurant는 QueryDSL 사용 시 필요하며, 없다면 삭제해도 무방합니다.
// import static com.simplecoding.michelin_back.restaurant.entity.QRestaurant.restaurant;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public List<MarkerDto> getRestaurantMarkers(Double lat, Double lng) {
        Double radius = 3.5;

        List<Restaurant> restaurants = restaurantRepository.findRestaurantsWithinRadius(lat, lng, radius);
        log.info("[지도 마커] 중심 좌표 ({}, {}) 기준 {}건 조회 완료", lat, lng, restaurants.size());

        return restaurants.stream()
                .map(res -> {
                    // 1. 이미지 리스트에서 메인('Y')인 것의 URL만 추출
                    // getImages()가 null일 경우를 대비해 안전하게 처리합니다.
                    String mainImageUrl = (res.getImages() != null) ? res.getImages().stream()
                            .filter(img -> "Y".equals(img.getIsMain()))
                            .map(RestaurantImage::getImageUrl)
                            .findFirst()
                            .orElse("default_image_url") : "default_image_url";

                    // 2. DTO에 담아서 반환
                    return MarkerDto.builder()
                            .id(res.getId())
                            .restaurantName(res.getRestaurantName())
                            .lat(res.getLat())
                            .lng(res.getLng())
                            .grade(res.getGrade())
                            .phone(res.getPhone())
                            .address(res.getAddress())
                            .category(res.getCategory())
                            .markerColor(determineMarkerColor(res.getGrade()))
                            .imageUrl(mainImageUrl)
                            .build();
                })
                .collect(Collectors.toList());
    } // ★ 이 닫는 괄호가 빠져 있었습니다!

    private String determineMarkerColor(String grade) {
        if (grade == null) return "blue";
        if (grade.contains("3스타")) return "red";
        if (grade.contains("2스타")) return "orange";
        if (grade.contains("1스타")) return "yellow";
        if (grade.contains("빕 구르망")) return "green";
        return "blue";
    }

    public List<MarkerDto> searchRestaurants(String name) {
        // 1. DB에서 리스트 가져오기
        List<Restaurant> restaurants = restaurantRepository.findByRestaurantNameContainingIgnoreCase(name);

        // 2. 스트림을 돌면서 하나씩 가공하기
        return restaurants.stream().map(r -> {
            MarkerDto dto = new MarkerDto();

            // 데이터 복사 (빨간줄 방지를 위해 r.getId() 등이 존재해야 함)
            dto.setId(r.getId());
            dto.setRestaurantName(r.getRestaurantName());
            dto.setLat(r.getLat());
            dto.setLng(r.getLng());
            dto.setGrade(r.getGrade());
            dto.setCategory(r.getCategory());
            dto.setAddress(r.getAddress());

            // [비즈니스 로직] 여기서 색상을 결정 (DTO는 모르게 함)
            String color = "#feb2b2"; // 기본색
            if ("3 Stars".equals(r.getGrade())) color = "#e62117";
            else if ("2 Stars".equals(r.getGrade())) color = "#ff5e5e";
            dto.setMarkerColor(color);

            // 이미지 처리
            if (r.getImages() != null && !r.getImages().isEmpty()) {
                dto.setImageUrl(r.getImages().get(0).getImageUrl());
            }

            return dto;
        }).collect(Collectors.toList());
    }
}