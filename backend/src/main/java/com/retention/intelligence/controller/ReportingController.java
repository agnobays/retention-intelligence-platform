package com.retention.intelligence.controller;

import com.retention.intelligence.service.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/v1/reports", "/reports"})
@RequiredArgsConstructor
@Tag(name = "Reporting", description = "Endpoints for Executive Retention Analytics and Metrics")
public class ReportingController {

    private static final Logger log = LoggerFactory.getLogger(ReportingController.class);
    private final ReportingService reportingService;

    @GetMapping("/dashboard")
    @Operation(summary = "Executive Dashboard KPI", description = "Returns high-level retention metrics and ARR saved")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        log.info("📊 API REQUEST: GET /api/v1/reports/dashboard - Fetching executive retention KPIs");
        return ResponseEntity.ok(reportingService.getExecutiveDashboardMetrics());
    }
}
