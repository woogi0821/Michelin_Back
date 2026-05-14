package com.simplecoding.michelin_back.restaurant.controller;

import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.MarkerDto;
import com.simplecoding.michelin_back.restaurant.dto.RestaurantRequestDto;
import com.simplecoding.michelin_back.restaurant.dto.RestaurantResponseDto;
import com.simplecoding.michelin_back.restaurant.dto.RestaurantSearchDto;
import com.simplecoding.michelin_back.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 음식점 목록 조회 (필터 + 페이지네이션)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RestaurantResponseDto>>> getList(
            RestaurantSearchDto searchDto) {
        return ResponseEntity.ok(
                ApiResponse.success(restaurantService.getList(searchDto)));
    }

    // 음식점 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponseDto>> getDetail(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(restaurantService.getDetail(id)));
    }

    // 음식점 등록
    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantResponseDto>> create(
            @RequestBody RestaurantRequestDto requestDto) {
        return ResponseEntity.ok(
                ApiResponse.success(restaurantService.create(requestDto)));
    }

    // 음식점 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponseDto>> update(
            @PathVariable Long id,
            @RequestBody RestaurantRequestDto requestDto) {
        return ResponseEntity.ok(
                ApiResponse.success(restaurantService.update(id, requestDto)));
    }

    // 음식점 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {
        restaurantService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("음식점이 삭제되었습니다."));
    }

    // 검색 자동완성
    @GetMapping("/autocomplete")
    public ResponseEntity<ApiResponse<List<RestaurantResponseDto>>> getAutocomplete(
            @RequestParam String keyword) {
        return ResponseEntity.ok(
                ApiResponse.success(restaurantService.getAutocomplete(keyword)));
    }

    // P4 연동 - 지도 마커 조회
    @GetMapping("/markers")
    public ResponseEntity<List<MarkerDto>> getMarkers(
            @RequestParam(name = "lat") Double lat,
            @RequestParam(name = "lng") Double lng) {
        log.info("[API 요청] 마커 조회 - 위도: {}, 경도: {}", lat, lng);
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