package com.simplecoding.michelin_back.social.service;

import com.simplecoding.michelin_back.social.entity.RestaurantBookmark;
import com.simplecoding.michelin_back.social.entity.RestaurantLike;
import com.simplecoding.michelin_back.social.repository.RestaurantBookmarkRepository;
import com.simplecoding.michelin_back.social.repository.RestaurantLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional // 데이터 변경 작업이므로 트랜잭션 처리가 필수입니다.
public class SocialService {
    private final RestaurantLikeRepository likeRepository;
    private final RestaurantBookmarkRepository bookmarkRepository;

    /**
     * 좋아요 토글 로직
     * @return true: 좋아요 추가됨, false: 좋아요 취소됨
     */
    public boolean toggleLike(Long userId, Long restaurantId) {
        Optional<RestaurantLike> existingLike = likeRepository.findByUserIdAndRestaurantId(userId, restaurantId);

        if (existingLike.isPresent()) {
            // 이미 좋아요가 있다면 삭제 (취소)
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            // 좋아요가 없다면 생성 (추가)
            RestaurantLike newLike = RestaurantLike.builder()
                    .userId(userId)
                    .restaurantId(restaurantId)
                    .build();
            likeRepository.save(newLike);
            return true;
        }
    }

    /**
     * 북마크 토글 로직
     * @return true: 저장됨, false: 저장 취소됨
     */
    public boolean toggleBookmark(Long userId, Long restaurantId) {
        Optional<RestaurantBookmark> existingBookmark = bookmarkRepository.findByUserIdAndRestaurantId(userId, restaurantId);

        if (existingBookmark.isPresent()) {
            // 이미 북마크가 있다면 삭제
            bookmarkRepository.delete(existingBookmark.get());
            return false;
        } else {
            // 북마크가 없다면 생성
            RestaurantBookmark newBookmark = RestaurantBookmark.builder()
                    .userId(userId)
                    .restaurantId(restaurantId)
                    .folderName("기본 폴더") // 필요시 폴더명 확장 가능
                    .build();
            bookmarkRepository.save(newBookmark);
            return true;
        }
    }
//    [추가] 조회 기능 (상태 유지용)
    @Transactional(readOnly = true)
    public boolean isLiked(Long userId, Long restaurantId) {
        return likeRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(Long userId, Long restaurantId) {
        return bookmarkRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }
}
