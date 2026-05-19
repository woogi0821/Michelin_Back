package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.DailyStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {
    Optional<DailyStats> findByStatDate(LocalDate statDate);
    boolean existsByStatDate(LocalDate statDate);
    List<DailyStats> findTop30ByOrderByStatDateDesc();
}
