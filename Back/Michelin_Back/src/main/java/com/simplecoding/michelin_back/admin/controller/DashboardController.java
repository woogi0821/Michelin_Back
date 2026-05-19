package com.simplecoding.michelin_back.admin.controller;

import com.simplecoding.michelin_back.admin.dto.DailyStatsDto;
import com.simplecoding.michelin_back.admin.service.DailyStatsService;
import com.simplecoding.michelin_back.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('A','S')")
public class DashboardController {

    private final DailyStatsService dailyStatsService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DailyStatsDto.Summary>> summary() {
        return ResponseEntity.ok(ApiResponse.success(dailyStatsService.getSummary()));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<List<DailyStatsDto.Response>>> stats() {
        return ResponseEntity.ok(ApiResponse.success(dailyStatsService.getRecent30()));
    }
}
