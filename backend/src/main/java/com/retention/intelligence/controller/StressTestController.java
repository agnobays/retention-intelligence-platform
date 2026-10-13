package com.retention.intelligence.controller;

import com.retention.intelligence.dto.StressTestResultDTO;
import com.retention.intelligence.service.StressTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/stress-test", "/stress-test"})
@RequiredArgsConstructor
@Tag(name = "Stress Testing & Benchmarking", description = "Endpoints for multi-tenant load testing and local business benchmarks")
public class StressTestController {

    private static final Logger log = LoggerFactory.getLogger(StressTestController.class);
    private final StressTestService stressTestService;

    @PostMapping("/run")
    @Operation(summary = "Run Local Business Stress Test", description = "Simulates high-concurrency ingestion and retention decisions on an isolated local business tenant (Kasi Express Logistics)")
    public ResponseEntity<StressTestResultDTO> runStressTest(
            @RequestParam(defaultValue = "100") int accountCount) {
        log.info("⚡ API REQUEST: POST /api/v1/stress-test/run with accountCount={}", accountCount);
        return ResponseEntity.ok(stressTestService.runLocalBusinessStressTest(accountCount));
    }

    @GetMapping("/run")
    @Operation(summary = "Run Local Business Stress Test (GET convenience)", description = "Convenience GET endpoint for triggering the stress test directly")
    public ResponseEntity<StressTestResultDTO> runStressTestGet(
            @RequestParam(defaultValue = "100") int accountCount) {
        return runStressTest(accountCount);
    }
}
