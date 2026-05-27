package com.simplecoding.michelin_back.review.controller;

import com.simplecoding.michelin_back.notification.sse.SseEmitters;
import com.simplecoding.michelin_back.review.dto.ReviewRequestDto;
import com.simplecoding.michelin_back.review.dto.ReviewResponseDto;
import com.simplecoding.michelin_back.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SseEmitters sseEmitters;

    /**
     * SSE 연결 엔드포인트
     * 프론트엔드에서 EventSource로 이 주소를 호출합니다.
     */
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam Long memberId) {
        // 타임아웃 1시간(밀리초 단위)
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);

        // memberId와 함께 add 호출
        return sseEmitters.add(memberId, emitter);
    }

    /**
     * 리뷰 및 대댓글 등록
     */
    @PostMapping
    public ResponseEntity<Long> createReview(@RequestBody ReviewRequestDto dto) {
        return ResponseEntity.ok(reviewService.createReview(dto));
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    /**
     * 좋아요/싫어요 토글
     * type: "LIKE" 또는 "DISLIKE" 등
     */
    @PostMapping("/{reviewId}/reaction")
    public ResponseEntity<Void> toggleReaction(
            @PathVariable Long reviewId,
            @RequestParam Long memberId,
            @RequestParam String type) {
        reviewService.toggleReaction(reviewId, memberId, type);
        return ResponseEntity.ok().build();
    }

    /**
     * 식당 평균 별점 조회
     */
    @GetMapping("/{restaurantId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(reviewService.getAverageRating(restaurantId));
    }

    /**
     * 식당별 리뷰 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@RequestParam Long restaurantId) {
        return ResponseEntity.ok(reviewService.getReviews(restaurantId));
    }
}