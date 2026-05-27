package com.simplecoding.michelin_back.social.repository;

import com.simplecoding.michelin_back.social.entity.RestaurantBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RestaurantBookmarkRepository extends JpaRepository<RestaurantBookmark, Long> {
    // [수정] UserId -> MemberId
    Optional<RestaurantBookmark> findByMemberIdAndRestaurantId(Long memberId, Long restaurantId);
    boolean existsByMemberIdAndRestaurantId(Long memberId, Long restaurantId);

    // [마이페이지] 내가 북마크한 매장 목록
    List<RestaurantBookmark> findAllByMemberId(Long memberId);

    // [마이페이지] 내 북마크 수
    long countByMemberId(Long memberId);
}