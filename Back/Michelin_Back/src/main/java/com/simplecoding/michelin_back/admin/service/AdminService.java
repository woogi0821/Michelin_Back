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

    /** 회원 목록 조회 */
    public Page<AdminDto.MemberResponse> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(this::toMemberResponse);
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

    /** 회원 탈퇴 처리 */
    @Transactional
    public void withdrawMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));
        member.suspend(LocalDate.of(9999, 12, 31)); // 영구 정지로 탈퇴 처리
    }

    // ── 리뷰 관리 ──────────────────────────────────────────

    /** 리뷰 목록 조회 (상태 필터) */
    public Page<AdminDto.ReviewResponse> getReviews(String status, Pageable pageable) {
        Page<RestaurantReview> page = switch (status.toUpperCase()) {
            case "ACTIVE"   -> adminRepository.findByIsDeletedAndIsBlindedOrderByCreatedAtDesc("N", "N", pageable);
            case "DELETED"  -> adminRepository.findByIsDeletedOrderByCreatedAtDesc("Y", pageable);
            case "BLINDED"  -> adminRepository.findByIsBlindedOrderByCreatedAtDesc("Y", pageable);
            default         -> adminRepository.findAllByOrderByCreatedAtDesc(pageable);
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

    // ── 문의 관리 ──────────────────────────────────────────

    /** 문의 목록 조회 */
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
        long todayNewReviews  = adminRepository.countTodayReviews(LocalDateTime.now().toLocalDate().atStartOfDay());
        long activePopups     = popubAdRepository.countByIsActive("Y");

        List<AdminDto.RecentRestaurant> recentRestaurants = restaurantRepository
                .findAll(Pageable.ofSize(5)).stream()
                .map(r -> AdminDto.RecentRestaurant.builder()
                        .id(r.getId())
                        .restaurantName(r.getRestaurantName())
                        .city(r.getCity())
                        .grade(r.getGrade())
                        .build())
                .collect(Collectors.toList());

        return AdminDto.DashboardStats.builder()
                .totalMembers(totalMembers)
                .totalRestaurants(totalRestaurants)
                .todayNewReviews(todayNewReviews)
                .activePopups(activePopups)
                .recentRestaurants(recentRestaurants)
                .build();
    }

    // ── 변환 메서드 ──────────────────────────────────────────

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
        return AdminDto.ReviewResponse.builder()
                .reviewId(r.getReviewId())
                .restaurantId(r.getRestaurantId())
                .memberId(r.getMemberId())
                .content(r.getContent())
                .rating(r.getRating())
                .isDeleted(r.getIsDeleted())
                .isBlinded(r.getIsBlinded())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    private AdminDto.InquiryResponse toInquiryResponse(Inquiry i) {
        return AdminDto.InquiryResponse.builder()
                .inquiryId(i.getInquiryId())
                .memberId(i.getMemberId())
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
