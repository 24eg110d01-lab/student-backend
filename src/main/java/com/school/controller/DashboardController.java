package com.school.controller;

import com.school.dto.DashboardStatsDto;
import com.school.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Analytics & Dashboard Statistics APIs")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get aggregated dashboard statistics, KPIs, and chart data")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
}
