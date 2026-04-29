package com.simplecoding.michelin_back.restaurant.service;

import com.simplecoding.michelin_back.common.MarkerDto;
import com.simplecoding.michelin_back.restaurant.entity.Restaurant;
import com.simplecoding.michelin_back.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j //로그(Log)를 남기기 위한 도구
@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    //    지도 마커용 맛집 리스트 조회
    @Transactional(readOnly = true)
    public List<MarkerDto> getRestaurantMarkers(Double lat, Double lng) {
        Double radius = 3.5; // 3.5km 반경

        // 1. DB에서 반경 내 식당 엔티티 리스트 조회
        List<Restaurant> restaurants = restaurantRepository.findRestaurantsWithinRadius(lat, lng, radius);

        log.info("[지도 마커] 중심 좌표 ({}, {}) 기준 {}건 조회 완료", lat, lng, restaurants.size());

        // 2. 엔티티 리스트를 MarkerDto 리스트로 변환 (Stream API 활용)
        return restaurants.stream()
                .map(res -> MarkerDto.builder() // 4
                        .id(res.getId())
                        .restaurantName(res.getRestaurantName())
                        .lat(res.getLat())
                        .lng(res.getLng())
                        .grade(res.getGrade())
                        .markerColor(determineMarkerColor(res.getGrade())) // 5
                        .build())
                .collect(Collectors.toList());
    }

    //    등급에 따른 마커 색상 결정 로직 (고도화 포인트)
    private String determineMarkerColor(String grade) {
        if (grade == null) return "gray";

        return switch (grade) { // 자바 14+ 최신 switch 문법
            case "3스타", "2스타", "1스타" -> "red";    // 스타급은 빨간색
            case "빕 구르망" -> "green";               // 가성비는 초록색
            case "선정 레스토랑" -> "blue";            // 일반 선정은 파란색
            default -> "gray";
        };
    }
}
