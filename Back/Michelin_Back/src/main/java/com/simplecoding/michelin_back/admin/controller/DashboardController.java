package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.entity.DailyStats;
import com.simplecoding.michelin_back.admin.service.DailyStatsService;
import com.simplecoding.michelin_back.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DashboardController {

    private final DailyStatsService dailyStatsService;

    // 기간별 통계 (기본: 최근 30일)
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<List<DailyStats>>> getStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from == null) from = LocalDate.now().minusDays(29);
        if (to == null)   to   = LocalDate.now();
        List<DailyStats> stats = dailyStatsService.getStats(from, to);
        return ResponseEntity.ok(new ApiResponse<>(true, "통계 조회 성공", stats, 0, stats.size()));
    }

    // 특정 날짜 통계
    @GetMapping("/stats/{date}")
    public ResponseEntity<ApiResponse<DailyStats>> getStatsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyStats stats = dailyStatsService.getStatsByDate(date);
        return ResponseEntity.ok(new ApiResponse<>(true, "일별 통계 조회 성공", stats, 0, 1));
    }
}
