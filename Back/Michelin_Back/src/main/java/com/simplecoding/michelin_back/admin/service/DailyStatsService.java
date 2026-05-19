package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.entity.DailyStats;
import com.simplecoding.michelin_back.admin.repository.DailyStatsRepository;
import com.simplecoding.michelin_back.admin.repository.InquiryRepository;
import com.simplecoding.michelin_back.admin.repository.PenaltyHistoryRepository;
import com.simplecoding.michelin_back.chatbot.repository.ChatbotSessionRepository;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyStatsService {

    private final DailyStatsRepository dailyStatsRepository;
    private final MemberRepository memberRepository;
    private final InquiryRepository inquiryRepository;
    private final PenaltyHistoryRepository penaltyHistoryRepository;
    private final ChatbotSessionRepository chatbotSessionRepository;

    // 매일 새벽 1시 집계
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void aggregateDaily() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        if (dailyStatsRepository.existsByStatsDate(yesterday)) {
            log.info("[DailyStats] 이미 집계된 날짜입니다: {}", yesterday);
            return;
        }

        LocalDateTime from = yesterday.atStartOfDay();
        LocalDateTime to = yesterday.plusDays(1).atStartOfDay();

        long newMembers    = memberRepository.countByInsertTimeBetween(from, to);
        long newPenalties  = penaltyHistoryRepository.countByStatusAndCreatedAtBetween("APPLIED", from, to);
        long totalInq      = inquiryRepository.countByCreatedAtBetween(from, to);
        long answeredInq   = inquiryRepository.countByStatusAndCreatedAtBetween("ANSWERED", from, to);

        DailyStats stats = DailyStats.builder()
                .statsDate(yesterday)
                .newMembers(newMembers)
                .newReviews(0L)         // 리뷰 파트(P3)에서 추가 예정
                .newPenalties(newPenalties)
                .totalInquiries(totalInq)
                .answeredInquiries(answeredInq)
                .chatbotSessions(chatbotSessionRepository.countByCreatedAtBetween(from, to))
                .totalTokens(0L)        // Gemini 응답에 토큰 수 미포함, 추후 Python 수정 시 반영
                .build();

        dailyStatsRepository.save(stats);
        log.info("[DailyStats] {} 집계 완료", yesterday);
    }

    // 기간별 통계 조회 (대시보드용)
    @Transactional(readOnly = true)
    public List<DailyStats> getStats(LocalDate from, LocalDate to) {
        return dailyStatsRepository.findByStatsDateBetweenOrderByStatsDateAsc(from, to);
    }

    // 특정 날짜 통계 조회
    @Transactional(readOnly = true)
    public DailyStats getStatsByDate(LocalDate date) {
        return dailyStatsRepository.findByStatsDate(date)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 날짜의 통계가 없습니다."));
    }
}
