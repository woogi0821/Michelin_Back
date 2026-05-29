package com.simplecoding.michelin_back.review.repository;

import com.simplecoding.michelin_back.restaurant.entity.Restaurant;
import com.simplecoding.michelin_back.review.entity.RestaurantReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantReviewRepository extends JpaRepository<RestaurantReview, Long> {

    // 1. 특정 식당의 삭제되지 않은 리뷰 목록 조회 (최신순)
    // 원글(parent IS NULL)만 먼저 가져오고, 엔티티의 children 설정을 통해 답글을 가져옵니다.
    List<RestaurantReview> findByRestaurantIdAndIsDeletedAndParentIsNullOrderByCreatedAtDesc(Long restaurantId, String isDeleted);

    // 2. 특정 식당의 평균 별점 계산 (소프트 삭제된 리뷰는 제외)
    @Query("SELECT AVG(r.rating) FROM RestaurantReview r WHERE r.restaurantId = :restaurantId AND r.isDeleted = 'N'")
    Double getAverageRating(@Param("restaurantId") Long restaurantId);

    // 3. 특정 리뷰를 삭제되지 않은 상태로 상세 조회
    Optional<RestaurantReview> findByReviewIdAndIsDeleted(Long reviewId, String isDeleted);

    // 4. 특정 부모 리뷰에 달린 삭제되지 않은 답글들 조회
    List<RestaurantReview> findByParentAndIsDeleted(RestaurantReview parent, String isDeleted);

    // 5. [마이페이지] 내 리뷰 목록 (원글만, 최신순)
    List<RestaurantReview> findByMemberIdAndIsDeletedAndParentIsNullOrderByCreatedAtDesc(Long memberId, String isDeleted);

    // 6. [마이페이지] 내 리뷰 수 카운트
    long countByMemberIdAndIsDeleted(Long memberId, String isDeleted);

    // 7. [마이페이지] 등급별 방문 매장 수 (리뷰 기준, 매장 중복 제거)
    @Query("SELECT r.grade, COUNT(DISTINCT rv.restaurantId) " +
           "FROM RestaurantReview rv " +
           "JOIN Restaurant r ON rv.restaurantId = r.id " +
           "WHERE rv.memberId = :memberId AND rv.isDeleted = 'N' " +
           "GROUP BY r.grade")
    List<Object[]> countVisitedByGrade(@Param("memberId") Long memberId);
}