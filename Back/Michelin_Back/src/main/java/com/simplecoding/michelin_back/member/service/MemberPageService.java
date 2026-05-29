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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        // 리뷰를 남긴 매장 수 (중복 제거) = countVisitedByGrade 결과 합산
        long visitCount = reviewRepository.countVisitedByGrade(memberId).stream()
                .mapToLong(row -> ((Number) row[1]).longValue())
                .sum();

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
                .visitCount(visitCount)
                .build();
    }

    /** 등급별 방문 현황 — 프론트 스펙: 배열로 반환 */
    public List<MemberPageDto.MichelinStatItem> getMichelinStats(Long memberId) {
        List<Object[]> rows = reviewRepository.countVisitedByGrade(memberId);

        // 방문 수 맵 (grade → count)
        Map<String, Long> visitedMap = rows.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).longValue()
                ));

        // 등급 정의 — label/color는 프론트 스펙 기준
        record GradeDef(String label, String grade, String color) {}
        List<GradeDef> defs = List.of(
                new GradeDef("★ 1 STAR",      "1스타",    "#DAA520"),
                new GradeDef("★★ 2 STAR",     "2스타",    "#C0C0C0"),
                new GradeDef("★★★ 3 STAR",    "3스타",    "#FFD700"),
                new GradeDef("BIB GOURMAND",   "빕 구르망", "#E8534A")
        );

        List<MemberPageDto.MichelinStatItem> result = new ArrayList<>();
        for (GradeDef def : defs) {
            long visited = visitedMap.getOrDefault(def.grade(), 0L);
            long total   = restaurantRepository.countByGradeAndStatus(def.grade(), "ACTIVE");
            result.add(MemberPageDto.MichelinStatItem.builder()
                    .label(def.label())
                    .grade(def.grade())
                    .visited(visited)
                    .total(total)
                    .color(def.color())
                    .borderColor(def.color())
                    .build());
        }
        return result;
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
                                .district(r.getDistrict())
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
                                .district(r.getDistrict())
                                .grade(r.getGrade())
                                .category(r.getCategory())
                                .createdAt(bm.getCreatedAt())
                                .build())
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
