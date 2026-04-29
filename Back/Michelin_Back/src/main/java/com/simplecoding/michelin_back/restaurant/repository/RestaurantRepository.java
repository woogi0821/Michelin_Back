package com.simplecoding.michelin_back.restaurant.repository;

import com.simplecoding.michelin_back.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // ✅ [핵심] 충전소 프로젝트의 반경 검색 쿼리 이식
    // 1.5km 이내의 미쉐린 식당을 위도/경도 기반으로 검색합니다.
    @Query(value = "SELECT * FROM ( " +
            "    SELECT r.*, " +
            "    (6371 * acos(cos(radians(:lat)) * cos(radians(r.lat)) * cos(radians(r.lng) - radians(:lng)) + " +
            "    sin(radians(:lat)) * sin(radians(r.lat)))) AS distance " +
            "    FROM RESTAURANTS r " +
            ") " +
            "WHERE distance <= :radius " +
            "ORDER BY distance", nativeQuery = true)
    List<Restaurant> findRestaurantsWithinRadius(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Double radius);

    // ✅ [추가] 등급별 필터링을 위한 쿼리 (필요 시 사용)
    List<Restaurant> findByGrade(String grade);
}
