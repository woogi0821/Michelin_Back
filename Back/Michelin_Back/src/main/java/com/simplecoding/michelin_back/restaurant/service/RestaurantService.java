package com.simplecoding.michelin_back.restaurant.service;

import com.simplecoding.michelin_back.common.CommonException;
import com.simplecoding.michelin_back.common.MarkerDto;
import com.simplecoding.michelin_back.restaurant.dto.RestaurantRequestDto;
import com.simplecoding.michelin_back.restaurant.dto.RestaurantResponseDto;
import com.simplecoding.michelin_back.restaurant.dto.RestaurantSearchDto;
import com.simplecoding.michelin_back.restaurant.entity.Restaurant;
import com.simplecoding.michelin_back.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    // 음식점 목록 조회 (필터 + 페이지네이션)
    public Page<RestaurantResponseDto> getList(RestaurantSearchDto searchDto) {
        Pageable pageable = PageRequest.of(
                searchDto.getPage(),
                searchDto.getSize(),
                Sort.by(Sort.Direction.DESC, "id")
        );

        Specification<Restaurant> spec = (root, query, cb) -> null;

        if (searchDto.getGrade() != null && !searchDto.getGrade().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("grade"), searchDto.getGrade()));
        }
        if (searchDto.getCity() != null && !searchDto.getCity().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("city"), searchDto.getCity()));
        }
        if (searchDto.getDistrict() != null && !searchDto.getDistrict().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("district"), searchDto.getDistrict()));
        }
        if (searchDto.getIsGreenStar() != null && !searchDto.getIsGreenStar().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isGreenStar"), searchDto.getIsGreenStar()));
        }
        if (searchDto.getKeyword() != null && !searchDto.getKeyword().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("restaurantName"), "%" + searchDto.getKeyword() + "%"));
        }

        return restaurantRepository.findAll(spec, pageable)
                .map(RestaurantResponseDto::new);
    }

    // 음식점 상세 조회 (조회수 +1)
    @Transactional
    public RestaurantResponseDto getDetail(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND, "음식점을 찾을 수 없습니다."));
        restaurant.increaseViewCount();
        return new RestaurantResponseDto(restaurant);
    }

    // 음식점 등록
    @Transactional
    public RestaurantResponseDto create(RestaurantRequestDto requestDto) {
        Restaurant restaurant = Restaurant.builder()
                .restaurantName(requestDto.getRestaurantName())
                .grade(requestDto.getGrade())
                .city(requestDto.getCity())
                .district(requestDto.getDistrict())
                .lat(requestDto.getLat())
                .lng(requestDto.getLng())
                .isGreenStar(requestDto.getIsGreenStar())
                .address(requestDto.getAddress())
                .kakaoPlaceUrl(requestDto.getKakaoPlaceUrl())
                .kakaoPlaceId(requestDto.getKakaoPlaceId())
                .phone(requestDto.getPhone())
                .build();
        return new RestaurantResponseDto(restaurantRepository.save(restaurant));
    }

    // 음식점 수정
    @Transactional
    public RestaurantResponseDto update(Long id, RestaurantRequestDto requestDto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND, "음식점을 찾을 수 없습니다."));
        restaurant.update(
                requestDto.getRestaurantName(),
                requestDto.getGrade(),
                requestDto.getCity(),
                requestDto.getDistrict(),
                requestDto.getAddress(),
                requestDto.getPhone(),
                requestDto.getIsGreenStar(),
                requestDto.getKakaoPlaceUrl(),
                requestDto.getKakaoPlaceId()
        );
        return new RestaurantResponseDto(restaurant);
    }

    // 음식점 삭제
    @Transactional
    public void delete(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND, "음식점을 찾을 수 없습니다."));
        restaurantRepository.delete(restaurant);
    }

    // 검색 자동완성
    public List<RestaurantResponseDto> getAutocomplete(String keyword) {
        Pageable pageable = PageRequest.of(0, 10);
        return restaurantRepository.findAutocomplete(keyword, pageable)
                .stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }

    // P4 연동 - 지도 마커용 음식점 조회 (반경 3.5km)
    public List<MarkerDto> getRestaurantMarkers(Double lat, Double lng) {
        Double radius = 3.5;
        List<Restaurant> restaurants =
                restaurantRepository.findRestaurantsWithinRadius(lat, lng, radius);
        log.info("[지도 마커] 중심 좌표 ({}, {}) 기준 {}건 조회 완료",
                lat, lng, restaurants.size());
        return restaurants.stream()
                .map(res -> MarkerDto.builder()
                        .id(res.getId())
                        .restaurantName(res.getRestaurantName())
                        .lat(res.getLat())
                        .lng(res.getLng())
                        .grade(res.getGrade())
                        .phone(res.getPhone())
                        .address(res.getAddress())
                        .markerColor(determineMarkerColor(res.getGrade()))
                        .build())
                .collect(Collectors.toList());
    }

    // 등급별 마커 색상 결정
    private String determineMarkerColor(String grade) {
        if (grade.contains("3스타")) return "red";
        if (grade.contains("2스타")) return "orange";
        if (grade.contains("1스타")) return "yellow";
        if (grade.contains("빕 구르망")) return "green";
        return "blue";
    }
}