package com.simplecoding.michelin_back.social.repository;

import com.simplecoding.michelin_back.social.entity.RestaurantLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantLikeRepository extends JpaRepository<RestaurantLike, Long> {
    // 특정 유저가 특정 식당에 좋아요를 눌렀는지 확인하는 메서드
    boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);

    // 좋아요 취소(삭제)를 위해 유저ID와 식당ID로 데이터를 찾는 메서드
    Optional<RestaurantLike> findByUserIdAndRestaurantId(Long userId, Long restaurantId);
}
