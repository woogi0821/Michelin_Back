package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.dto.AdminDto;
import com.simplecoding.michelin_back.admin.entity.Inquiry;
import com.simplecoding.michelin_back.admin.repository.AdminRepository;
import com.simplecoding.michelin_back.admin.repository.InquiryRepository;
import com.simplecoding.michelin_back.common.CommonException;
import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import com.simplecoding.michelin_back.popub.repository.PopubAdRepository;
import com.simplecoding.michelin_back.restaurant.entity.Restaurant;
import com.simplecoding.michelin_back.restaurant.repository.RestaurantRepository;
import com.simplecoding.michelin_back.review.entity.RestaurantReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final InquiryRepository inquiryRepository;
    private final RestaurantRepository restaurantRepository;
    private final PopubAdRepository popubAdRepository;

    // ── 회원 관리 ──────────────────────────────────────────

    /** 회원 목록 조회 (keyword 검색 포함) */
    public Page<AdminDto.MemberResponse> getMembers(String keyword, Pageable pageable) {
        return memberRepository.searchByKeyword(keyword, pageable).map(this::toMemberResponse);
    }

    /** 회원 정지 */
    @Transactional
    public void suspendMember(Long memberId, LocalDate suspendedUntil) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));
        member.addPenalty();
        member.suspend(suspendedUntil);
    }

    /** 회원 정지 해제 */
    @Transactional
    public void releaseMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));
        member.releaseSuspension();
    }

    /** 회원 탈퇴 처리 (영구 정지) */
    @Transactional
    public void withdrawMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));
        member.suspend(LocalDate.of(9999, 12, 31));
    }

    // ── 리뷰 관리 ──────────────────────────────────────────

    /**
     * 리뷰 목록 조회 (상태 필터)
     * 프론트 status: ALL | ACTIVE | REPORTED(=BLINDED) | DELETED
     */
    public Page<AdminDto.ReviewResponse> getReviews(String status, Pageable pageable) {
        Page<RestaurantReview> page = switch (status.toUpperCase()) {
            case "ACTIVE"              -> adminRepository.findByIsDeletedAndIsBlindedOrderByCreatedAtDesc("N", "N", pageable);
            case "DELETED"             -> adminRepository.findByIsDeletedOrderByCreatedAtDesc("Y", pageable);
            case "REPORTED", "BLINDED" -> adminRepository.findByIsBlindedOrderByCreatedAtDesc("Y", pageable);
            default                    -> adminRepository.findAllByOrderByCreatedAtDesc(pageable);
        };
        return page.map(this::toReviewResponse);
    }

    /** 리뷰 삭제 */
    @Transactional
    public void deleteReview(Long reviewId) {
        RestaurantReview review = adminRepository.findById(reviewId)
                .orElseThrow(() -> CommonException.notFound("리뷰를 찾을 수 없습니다."));
        review.softDelete();
    }

    /** 리뷰 복구 */
    @Transactional
    public void restoreReview(Long reviewId) {
        RestaurantReview review = adminRepository.findById(reviewId)
                .orElseThrow(() -> CommonException.notFound("리뷰를 찾을 수 없습니다."));
        review.restore();
    }

    /** 리뷰 블라인드 */
    @Transactional
    public void blindReview(Long reviewId) {
        RestaurantReview review = adminRepository.findById(reviewId)
                .orElseThrow(() -> CommonException.notFound("리뷰를 찾을 수 없습니다."));
        review.blind();
    }

    // ── 레스토랑 관리 ──────────────────────────────────────────

    /** 레스토랑 목록 조회 (관리자용: DELETED 포함, 키워드/상태 필터) */
    public Page<AdminDto.RestaurantResponse> getRestaurants(String keyword, String status, Pageable pageable) {
        return restaurantRepository.findAllForAdmin(keyword, status, pageable)
                .map(this::toRestaurantResponse);
    }

    /** 레스토랑 삭제 (Soft Delete) */
    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> CommonException.notFound("레스토랑을 찾을 수 없습니다."));
        restaurant.softDelete();
    }

    /** 레스토랑 복구 */
    @Transactional
    public void restoreRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> CommonException.notFound("레스토랑을 찾을 수 없습니다."));
        restaurant.restore();
    }

    // ── 문의 관리 ──────────────────────────────────────────

    /** 문의 목록 조회 (status 필터) */
    public Page<AdminDto.InquiryResponse> getInquiries(String status, Pageable pageable) {
        Page<Inquiry> page = (status == null || status.isBlank())
                ? inquiryRepository.findAllByOrderByCreatedAtDesc(pageable)
                : inquiryRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return page.map(this::toInquiryResponse);
    }

    /** 문의 답변 처리 */
    @Transactional
    public void answerInquiry(Long inquiryId, Long adminId, String answer) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> CommonException.notFound("문의를 찾을 수 없습니다."));
        inquiry.answer(adminId, answer);
    }

    // ── 대시보드 ──────────────────────────────────────────

    /** 대시보드 통계 */
    public AdminDto.DashboardStats getDashboardStats() {
        long totalMembers     = memberRepository.count();
        long totalRestaurants = restaurantRepository.count();
        long newReviewsToday  = adminRepository.countTodayReviews(LocalDateTime.now().toLocalDate().atStartOfDay());
        long activePopups     = popubAdRepository.countByIsActive("Y");

        List<AdminDto.RecentRestaurant> recentRestaurants = restaurantRepository
                .findRecentRestaurants(PageRequest.of(0, 5)).stream()
                .map(r -> AdminDto.RecentRestaurant.builder()
                        .restaurantId(r.getId())
                        .name(r.getRestaurantName())
                        .category(r.getCategory())
                        .regDate(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return AdminDto.DashboardStats.builder()
                .totalMembers(totalMembers)
                .totalRestaurants(totalRestaurants)
                .newReviewsToday(newReviewsToday)
                .activePopups(activePopups)
                .recentRestaurants(recentRestaurants)
                .build();
    }

    // ── 변환 메서드 ──────────────────────────────────────────

    private AdminDto.RestaurantResponse toRestaurantResponse(Restaurant r) {
        return AdminDto.RestaurantResponse.builder()
                .id(r.getId())
                .restaurantName(r.getRestaurantName())
                .grade(r.getGrade())
                .city(r.getCity())
                .district(r.getDistrict())
                .address(r.getAddress())
                .category(r.getCategory())
                .isGreenStar(r.getIsGreenStar())
                .viewCount(r.getViewCount())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private AdminDto.MemberResponse toMemberResponse(Member m) {
        return AdminDto.MemberResponse.builder()
                .memberId(m.getMemberId())
                .loginId(m.getLoginId())
                .name(m.getName())
                .email(m.getEmail())
                .phone(m.getPhone())
                .status(m.getStatus())
                .memberGrade(m.getMemberGrade())
                .provider(m.getProvider())
                .penaltyCount(m.getPenaltyCount())
                .suspendedUntil(m.getSuspendedUntil())
                .insertTime(m.getInsertTime())
                .build();
    }

    private AdminDto.ReviewResponse toReviewResponse(RestaurantReview r) {
        String writer = memberRepository.findById(r.getMemberId())
                .map(Member::getLoginId)
                .orElse("알 수 없음");

        String status;
        if ("Y".equals(r.getIsDeleted()))      status = "DELETED";
        else if ("Y".equals(r.getIsBlinded())) status = "REPORTED";
        else                                    status = "ACTIVE";

        return AdminDto.ReviewResponse.builder()
                .id(r.getReviewId())
                .writer(writer)
                .content(r.getContent())
                .reportCount(0)
                .createdAt(r.getCreatedAt())
                .status(status)
                .build();
    }

    private AdminDto.InquiryResponse toInquiryResponse(Inquiry i) {
        String memberName = memberRepository.findById(i.getMemberId())
                .map(Member::getName)
                .orElse("알 수 없음");

        return AdminDto.InquiryResponse.builder()
                .id(i.getInquiryId())
                .memberId(i.getMemberId())
                .memberName(memberName)
                .category(i.getCategory())
                .title(i.getTitle())
                .content(i.getContent())
                .status(i.getStatus())
                .answer(i.getAnswer())
                .answeredAt(i.getAnsweredAt())
                .createdAt(i.getCreatedAt())
                .build();
    }
}
