package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.review.entity.RestaurantReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AdminRepository extends JpaRepository<RestaurantReview, Long> {

    /** 오늘 신규 리뷰 수 */
    @Query("SELECT COUNT(r) FROM RestaurantReview r WHERE r.createdAt >= :startOfDay")
    long countTodayReviews(@Param("startOfDay") LocalDateTime startOfDay);

    /** 전체 리뷰 목록 */
    Page<RestaurantReview> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /** ACTIVE: 삭제X, 블라인드X */
    Page<RestaurantReview> findByIsDeletedAndIsBlindedOrderByCreatedAtDesc(
            String isDeleted, String isBlinded, Pageable pageable);

    /** DELETED */
    Page<RestaurantReview> findByIsDeletedOrderByCreatedAtDesc(String isDeleted, Pageable pageable);

    /** BLINDED */
    Page<RestaurantReview> findByIsBlindedOrderByCreatedAtDesc(String isBlinded, Pageable pageable);
}
