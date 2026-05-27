package com.simplecoding.michelin_back.social.repository;

import com.simplecoding.michelin_back.social.entity.RestaurantLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantLikeRepository extends JpaRepository<RestaurantLike, Long> {
    // [수정] findByUserId -> findByMemberId
    Optional<RestaurantLike> findByMemberIdAndRestaurantId(Long memberId, Long restaurantId);

    // [마이페이지] 내가 좋아요한 매장 목록
    List<RestaurantLike> findAllByMemberId(Long memberId);

    // [마이페이지] 내 좋아요 수
    long countByMemberId(Long memberId);
}
