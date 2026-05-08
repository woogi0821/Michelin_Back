package com.simplecoding.michelin_back.restaurant.repository;

import com.simplecoding.michelin_back.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>,
        JpaSpecificationExecutor<Restaurant> {

    // 검색 자동완성 (이름 LIKE + 조회수 높은 순)
    @Query("SELECT r FROM Restaurant r " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "ORDER BY r.viewCount DESC")
    List<Restaurant> findAutocomplete(@Param("keyword") String keyword, Pageable pageable);

    // 등급별 조회
    Page<Restaurant> findByGrade(String grade, Pageable pageable);

    // 도시별 조회
    Page<Restaurant> findByCity(String city, Pageable pageable);

    // 도시 + 구 조회
    Page<Restaurant> findByCityAndDistrict(String city, String district, Pageable pageable);

    // 그린스타 조회
    Page<Restaurant> findByIsGreenStar(String isGreenStar, Pageable pageable);
}