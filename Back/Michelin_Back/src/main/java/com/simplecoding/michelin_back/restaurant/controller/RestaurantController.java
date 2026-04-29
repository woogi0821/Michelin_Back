package com.simplecoding.michelin_back.restaurant.controller;

import com.simplecoding.michelin_back.common.MarkerDto;
import com.simplecoding.michelin_back.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController // 이 클래스가 JSON 데이터를 반환하는 API 컨트롤러임을 선언
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    //    지도 마커 조회를 위한 API
    @GetMapping("/markers")
    public ResponseEntity<List<MarkerDto>> getMarkers(
            @RequestParam(name = "lat") Double lat,  // @RequestParam 자바 변수로 매핑
            @RequestParam(name = "lng") Double lng) {

        log.info("[API 요청] 마커 조회 - 위도: {}, 경도: {}", lat, lng);
        // 좌표값이 누락된 경우 빈 리스트 반환 (방어 코드)
        if (lat == null || lng == null || lat == 0.0 || lng == 0.0) {
            return ResponseEntity.ok(List.of());
        }

        try {
            List<MarkerDto> markers = restaurantService.getRestaurantMarkers(lat, lng);
            return ResponseEntity.ok(markers);
        } catch (Exception e) {
            log.error("마커 조회 중 서버 에러 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
