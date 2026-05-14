package com.simplecoding.michelin_back.social.controller;

import com.simplecoding.michelin_back.social.dto.SocialRequestDto;
import com.simplecoding.michelin_back.social.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SocialController {

    private final SocialService socialService;

    // 1. 좋아요 토글 (DTO 하나만 남기기!)
    @PostMapping("/like")
    public ResponseEntity<Boolean> toggleLike(@RequestBody SocialRequestDto dto) {
        return ResponseEntity.ok(socialService.toggleLike(dto.getUserId(), dto.getRestaurantId()));
    }

    // 2. 북마크 토글 (DTO 하나만 남기기!)
    @PostMapping("/bookmark")
    public ResponseEntity<Boolean> toggleBookmark(@RequestBody SocialRequestDto dto) {
        return ResponseEntity.ok(socialService.toggleBookmark(dto.getUserId(), dto.getRestaurantId()));
    }

    // 3. 상태 조회 (이건 GET 방식이라 겹치지 않으니 그대로 둡니다)
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getStatus(
            @RequestParam Long userId,
            @RequestParam Long restaurantId) {

        boolean liked = socialService.isLiked(userId, restaurantId);
        boolean bookmarked = socialService.isBookmarked(userId, restaurantId);

        return ResponseEntity.ok(Map.of(
                "isLiked", liked,
                "isBookmarked", bookmarked
        ));
    }
}