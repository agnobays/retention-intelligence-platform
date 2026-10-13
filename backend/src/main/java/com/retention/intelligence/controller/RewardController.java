package com.retention.intelligence.controller;

import com.retention.intelligence.dto.RewardCatalogDTO;
import com.retention.intelligence.dto.RewardDispatchLogDTO;
import com.retention.intelligence.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * REST API for managing configurable reward catalogues and dispatching/tracking
 * loyalty rewards (loyalty points, cashback, vouchers, fee waivers, lifestyle experiences).
 */
@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    // ── Catalogue Endpoints ────────────────────────────────────────────────────

    @GetMapping("/catalog")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    public ResponseEntity<List<RewardCatalogDTO>> getCatalogue(@RequestParam UUID companyId) {
        return ResponseEntity.ok(rewardService.getCatalogueForCompany(companyId));
    }

    @PostMapping("/catalog")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<RewardCatalogDTO> addToCatalogue(
            @RequestParam UUID companyId,
            @Valid @RequestBody RewardCatalogDTO dto) {
        return ResponseEntity.ok(rewardService.addToCatalogue(companyId, dto));
    }

    @DeleteMapping("/catalog/{rewardId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<Void> deactivateReward(@PathVariable UUID rewardId) {
        rewardService.deactivateReward(rewardId);
        return ResponseEntity.noContent().build();
    }

    // ── Dispatch Endpoints ─────────────────────────────────────────────────────

    /**
     * POST /api/v1/rewards/dispatch/{customerId}
     * Manually dispatches a reward to a customer (e.g. triggered by manager).
     *
     * Query params:
     *   - rewardCatalogId (optional): specific catalogue entry to dispatch
     *   - channel (optional, default EMAIL): EMAIL | SMS | API | MANUAL
     */
    @PostMapping("/dispatch/{customerId}")
    public ResponseEntity<RewardDispatchLogDTO> dispatchReward(
            @PathVariable UUID customerId,
            @RequestParam(required = false) UUID rewardCatalogId,
            @RequestParam(defaultValue = "EMAIL") String channel) {
        return ResponseEntity.ok(rewardService.dispatchReward(customerId, rewardCatalogId, channel));
    }

    /**
     * PATCH /api/v1/rewards/dispatch/{dispatchLogId}/redeem
     * Marks a dispatched reward as redeemed by the customer.
     */
    @PatchMapping("/dispatch/{dispatchLogId}/redeem")
    public ResponseEntity<RewardDispatchLogDTO> markRedeemed(@PathVariable UUID dispatchLogId) {
        return ResponseEntity.ok(rewardService.markRedeemed(dispatchLogId));
    }

    // ── History Endpoints ──────────────────────────────────────────────────────

    /**
     * GET /api/v1/rewards/history/{customerId}
     * Returns the full reward dispatch history for a specific customer.
     */
    @GetMapping("/history/{customerId}")
    public ResponseEntity<List<RewardDispatchLogDTO>> getDispatchHistory(@PathVariable UUID customerId) {
        return ResponseEntity.ok(rewardService.getDispatchHistory(customerId));
    }
}
