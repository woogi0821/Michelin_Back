package com.simplecoding.michelin_back.member.service;

import com.simplecoding.michelin_back.common.CommonException;
import com.simplecoding.michelin_back.member.dto.MemberPageDto;
import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import com.simplecoding.michelin_back.restaurant.entity.Restaurant;
import com.simplecoding.michelin_back.restaurant.repository.RestaurantRepository;
import com.simplecoding.michelin_back.review.repository.RestaurantReviewRepository;
import com.simplecoding.michelin_back.social.repository.RestaurantBookmarkRepository;
import com.simplecoding.michelin_back.social.repository.RestaurantLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPageService {

    private final MemberRepository memberRepository;
    private final RestaurantReviewRepository reviewRepository;
    private final RestaurantLikeRepository likeRepository;
    private final RestaurantBookmarkRepository bookmarkRepository;
    private final RestaurantRepository restaurantRepository;

    /** 프로필 + 카운트 */
    public MemberPageDto.ProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));

        long reviewCount   = reviewRepository.countByMemberIdAndIsDeleted(memberId, "N");
        long likeCount     = likeRepository.countByMemberId(memberId);
        long bookmarkCount = bookmarkRepository.countByMemberId(memberId);

        return MemberPageDto.ProfileResponse.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .email(member.getEmail())
                .loginId(member.getLoginId())
                .provider(member.getProvider())
                .status(member.getStatus())
                .reviewCount(reviewCount)
                .likeCount(likeCount)
                .bookmarkCount(bookmarkCount)
                .build();
    }

    /** 등급별 방문 현황 (리뷰를 남긴 매장 기준) */
    public MemberPageDto.MichelinStatsResponse getMichelinStats(Long memberId) {
        List<Object[]> rows = reviewRepository.countVisitedByGrade(memberId);

        long oneStar = 0, twoStar = 0, threeStar = 0, bibGourmand = 0;
        for (Object[] row : rows) {
            String grade = (String) row[0];
            long count   = ((Number) row[1]).longValue();
            switch (grade) {
                case "1성"   -> oneStar     = count;
                case "2성"   -> twoStar     = count;
                case "3성"   -> threeStar   = count;
                case "빕구르망" -> bibGourmand = count;
            }
        }

        return MemberPageDto.MichelinStatsResponse.builder()
                .oneStar(oneStar)
                .twoStar(twoStar)
                .threeStar(threeStar)
                .bibGourmand(bibGourmand)
                .total(oneStar + twoStar + threeStar + bibGourmand)
                .build();
    }

    /** 내 리뷰 목록 (원글만, 최신순) */
    public List<MemberPageDto.MyReviewResponse> getMyReviews(Long memberId) {
        return reviewRepository
                .findByMemberIdAndIsDeletedAndParentIsNullOrderByCreatedAtDesc(memberId, "N")
                .stream()
                .map(rv -> {
                    String restaurantName = restaurantRepository.findById(rv.getRestaurantId())
                            .map(Restaurant::getRestaurantName)
                            .orElse("알 수 없는 매장");
                    return MemberPageDto.MyReviewResponse.builder()
                            .reviewId(rv.getReviewId())
                            .restaurantId(rv.getRestaurantId())
                            .restaurantName(restaurantName)
                            .content(rv.getContent())
                            .rating(rv.getRating())
                            .createdAt(rv.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /** 좋아요한 매장 목록 */
    public List<MemberPageDto.MyRestaurantResponse> getMyLikes(Long memberId) {
        return likeRepository.findAllByMemberId(memberId).stream()
                .map(like -> restaurantRepository.findById(like.getRestaurantId())
                        .map(r -> MemberPageDto.MyRestaurantResponse.builder()
                                .restaurantId(r.getId())
                                .restaurantName(r.getRestaurantName())
                                .address(r.getAddress())
                                .grade(r.getGrade())
                                .category(r.getCategory())
                                .createdAt(like.getCreatedAt())
                                .build())
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /** 북마크한 매장 목록 */
    public List<MemberPageDto.MyRestaurantResponse> getMyBookmarks(Long memberId) {
        return bookmarkRepository.findAllByMemberId(memberId).stream()
                .map(bm -> restaurantRepository.findById(bm.getRestaurantId())
                        .map(r -> MemberPageDto.MyRestaurantResponse.builder()
                                .restaurantId(r.getId())
                                .restaurantName(r.getRestaurantName())
                                .address(r.getAddress())
                                .grade(r.getGrade())
                                .category(r.getCategory())
                                .createdAt(bm.getCreatedAt())
                                .build())
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
