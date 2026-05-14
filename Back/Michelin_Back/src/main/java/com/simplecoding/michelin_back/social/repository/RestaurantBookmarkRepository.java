package com.simplecoding.michelin_back.social.repository;

import com.simplecoding.michelin_back.social.entity.RestaurantBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantBookmarkRepository extends JpaRepository<RestaurantBookmark, Long> {

    boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);

    Optional<RestaurantBookmark> findByUserIdAndRestaurantId(Long userId, Long restaurantId);
}
