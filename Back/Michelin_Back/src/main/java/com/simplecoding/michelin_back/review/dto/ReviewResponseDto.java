package com.simplecoding.michelin_back.review.dto;

import com.simplecoding.michelin_back.review.entity.RestaurantReview;
import com.simplecoding.michelin_back.review.entity.ReviewReaction;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private Long memberId;
    private String content;
    private Integer rating;
    private String isDeleted;
    private LocalDateTime createdAt;
    private long likeCount;
    private long dislikeCount;
    private List<ReviewResponseDto> children;

    // ✅ 엔티티 → DTO 변환 생성자
    public ReviewResponseDto(RestaurantReview review) {
        this.reviewId = review.getReviewId();
        this.memberId = review.getMemberId();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.isDeleted = review.getIsDeleted();
        this.createdAt = review.getCreatedAt();

        // 좋아요/싫어요 개수
        this.likeCount = review.getReactions().stream()
                .filter(r -> "LIKE".equals(r.getReactionType()))
                .count();
        this.dislikeCount = review.getReactions().stream()
                .filter(r -> "DISLIKE".equals(r.getReactionType()))
                .count();

        // 삭제되지 않은 답글만 포함
        this.children = review.getChildren().stream()
                .filter(child -> "N".equals(child.getIsDeleted()))
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }
}