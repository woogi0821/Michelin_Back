package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.DailyStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {

    // 특정 날짜 통계 조회
    Optional<DailyStats> findByStatsDate(LocalDate statsDate);

    // 최근 N일 통계 (대시보드 차트용)
    List<DailyStats> findByStatsDateBetweenOrderByStatsDateAsc(LocalDate from, LocalDate to);

    // 해당 날짜 통계 존재 여부 (Scheduler 중복 방지)
    boolean existsByStatsDate(LocalDate statsDate);
}
