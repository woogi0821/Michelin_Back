package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.dto.DailyStatsDto;
import com.simplecoding.michelin_back.admin.entity.DailyStats;
import com.simplecoding.michelin_back.admin.repository.DailyStatsRepository;
import com.simplecoding.michelin_back.admin.repository.InquiryRepository;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import com.simplecoding.michelin_back.review.repository.RestaurantReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyStatsService {

    private final DailyStatsRepository dailyStatsRepository;
    private final MemberRepository memberRepository;
    private final RestaurantReviewRepository reviewRepository;
    private final InquiryRepository inquiryRepository;

    /** 매일 새벽 1시 집계 */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void aggregateDaily() {
        LocalDate today = LocalDate.now();
        if (dailyStatsRepository.existsByStatDate(today)) {
            log.info("[DailyStats] 오늘({}) 통계 이미 존재. 건너뜀.", today);
            return;
        }

        LocalDate yesterday   = today.minusDays(1);
        LocalDateTime dayStart = yesterday.atStartOfDay();
        LocalDateTime dayEnd   = today.atStartOfDay();

        long totalMembers    = memberRepository.count();
        long newMembers      = memberRepository.findAll().stream()
                .filter(m -> m.getInsertTime() != null
                        && m.getInsertTime().isAfter(dayStart)
                        && m.getInsertTime().isBefore(dayEnd))
                .count();
        long activeReviews   = reviewRepository.count();
        long totalInquiries  = inquiryRepository.count();
        long pendingInquiries = inquiryRepository.countByStatus("PENDING");

        DailyStats stats = DailyStats.builder()
                .statDate(today)
                .totalMembers(totalMembers)
                .newMembers(newMembers)
                .activeReviews(activeReviews)
                .totalInquiries(totalInquiries)
                .pendingInquiries(pendingInquiries)
                .build();

        dailyStatsRepository.save(stats);
        log.info("[DailyStats] {} 집계 완료. 총 회원={}, 신규={}", today, totalMembers, newMembers);
    }

    /** 최근 30일 통계 목록 */
    public List<DailyStatsDto.Response> getRecent30() {
        return dailyStatsRepository.findTop30ByOrderByStatDateDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** 오늘 대시보드 요약 */
    public DailyStatsDto.Summary getSummary() {
        LocalDate today = LocalDate.now();
        Optional<DailyStats> opt = dailyStatsRepository.findByStatDate(today);

        if (opt.isEmpty()) {
            // 아직 집계 전이면 실시간 간이 집계
            return DailyStatsDto.Summary.builder()
                    .today(today)
                    .totalMembers(memberRepository.count())
                    .newMembersToday(0L)
                    .activeReviews(reviewRepository.count())
                    .pendingInquiries(inquiryRepository.countByStatus("PENDING"))
                    .build();
        }

        DailyStats s = opt.get();
        return DailyStatsDto.Summary.builder()
                .today(s.getStatDate())
                .totalMembers(s.getTotalMembers())
                .newMembersToday(s.getNewMembers())
                .activeReviews(s.getActiveReviews())
                .pendingInquiries(s.getPendingInquiries())
                .build();
    }

    private DailyStatsDto.Response toResponse(DailyStats s) {
        return DailyStatsDto.Response.builder()
                .statId(s.getStatId())
                .statDate(s.getStatDate())
                .totalMembers(s.getTotalMembers())
                .newMembers(s.getNewMembers())
                .activeReviews(s.getActiveReviews())
                .totalInquiries(s.getTotalInquiries())
                .pendingInquiries(s.getPendingInquiries())
                .insertTime(s.getInsertTime())
                .build();
    }
}
