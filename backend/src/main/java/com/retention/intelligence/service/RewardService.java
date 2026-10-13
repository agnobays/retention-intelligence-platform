package com.retention.intelligence.service;

import com.retention.intelligence.dto.RewardCatalogDTO;
import com.retention.intelligence.dto.RewardDispatchLogDTO;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.entity.RewardCatalog;
import com.retention.intelligence.entity.RewardDispatchLog;
import com.retention.intelligence.exception.ResourceNotFoundException;
import com.retention.intelligence.repository.CustomerRepository;
import com.retention.intelligence.repository.RewardCatalogRepository;
import com.retention.intelligence.repository.RewardDispatchLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardService {

    private static final Logger log = LoggerFactory.getLogger(RewardService.class);

    private final RewardCatalogRepository rewardCatalogRepository;
    private final RewardDispatchLogRepository rewardDispatchLogRepository;
    private final CustomerRepository customerRepository;

    // ── Catalogue Management ──────────────────────────────────────────────────

    /**
     * Returns all active rewards configured for a company.
     */
    public List<RewardCatalogDTO> getCatalogueForCompany(UUID companyId) {
        return rewardCatalogRepository
                .findByCompanyIdAndActiveTrueOrderByMinFrustrationScoreDesc(companyId)
                .stream()
                .map(this::toCatalogDTO)
                .collect(Collectors.toList());
    }

    /**
     * Adds a new reward type to the company's catalogue.
     */
    @Transactional
    public RewardCatalogDTO addToCatalogue(UUID companyId, RewardCatalogDTO dto) {
        // Company lookup via any existing customer for this company (lazy approach — no direct company repo needed)
        RewardCatalog reward = new RewardCatalog();

        // Set company by proxy lookup — re-uses existing customer's company link
        customerRepository.findFirstByCompanyId(companyId).ifPresent(c -> reward.setCompany(c.getCompany()));

        reward.setRewardType(dto.getRewardType().toUpperCase());
        reward.setRewardName(dto.getRewardName());
        reward.setRewardValue(dto.getRewardValue());
        reward.setTargetSegment(dto.getTargetSegment() != null ? dto.getTargetSegment().toUpperCase() : "ALL");
        reward.setMinFrustrationScore(dto.getMinFrustrationScore() != null ? dto.getMinFrustrationScore() : 50);
        reward.setRequiresApproval(dto.getRequiresApproval() != null ? dto.getRequiresApproval() : false);
        reward.setActive(true);

        RewardCatalog saved = rewardCatalogRepository.save(reward);
        log.info("✅ [REWARD CATALOGUE] New reward added: '{}' ({}) for company {} targeting segment {}",
                saved.getRewardName(), saved.getRewardType(), companyId, saved.getTargetSegment());
        return toCatalogDTO(saved);
    }

    /**
     * Deactivates a reward from the catalogue (soft delete).
     */
    @Transactional
    public void deactivateReward(UUID rewardId) {
        RewardCatalog reward = rewardCatalogRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found: " + rewardId));
        reward.setActive(false);
        rewardCatalogRepository.save(reward);
        log.info("🚫 [REWARD CATALOGUE] Reward '{}' deactivated.", reward.getRewardName());
    }

    // ── Reward Selection (used by DecisionEngineService) ─────────────────────

    /**
     * Selects the best reward from the company catalogue for the given customer segment
     * and frustration level. Falls back to a hardcoded default if no catalogue entry exists.
     */
    public RewardCatalog selectBestReward(UUID companyId, String segment, int frustrationScore) {
        List<RewardCatalog> matches = rewardCatalogRepository.findBestMatches(companyId, segment, frustrationScore);
        if (!matches.isEmpty()) {
            RewardCatalog selected = matches.get(0);
            log.info("🎯 [REWARD ENGINE] Matched reward '{}' ({}) for segment={} frustration={}",
                    selected.getRewardName(), selected.getRewardType(), segment, frustrationScore);
            return selected;
        }
        log.warn("⚠️ [REWARD ENGINE] No catalogue match for segment={} frustration={}. Using fallback.", segment, frustrationScore);
        return null; // DecisionEngineService handles fallback
    }

    // ── Reward Dispatch ───────────────────────────────────────────────────────

    /**
     * Dispatches a reward to a customer and logs it.
     * Called from the Camunda DispatchRewardDelegate after manager approval.
     */
    @Transactional
    public RewardDispatchLogDTO dispatchReward(UUID customerId, UUID rewardCatalogId, String channel) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));

        RewardCatalog catalog = rewardCatalogId != null
                ? rewardCatalogRepository.findById(rewardCatalogId).orElse(null)
                : null;

        // Use catalog values or fall back to what's already on the customer record
        String rewardType  = catalog != null ? catalog.getRewardType()  : "CUSTOM";
        String rewardName  = catalog != null ? catalog.getRewardName()  : (customer.getRewardCategory() != null ? customer.getRewardCategory() : "Loyalty Concession");
        String rewardValue = catalog != null ? catalog.getRewardValue() : (customer.getRewardValue()    != null ? customer.getRewardValue()    : "Standard Retention Benefit");

        String actualChannel = channel;
        if (actualChannel == null || actualChannel.equalsIgnoreCase("AUTO")) {
            actualChannel = customer.getPreferredContactMethod() != null ? customer.getPreferredContactMethod() : "EMAIL";
        } else {
            actualChannel = actualChannel.toUpperCase();
        }

        RewardDispatchLog logEntry = new RewardDispatchLog();
        logEntry.setCustomer(customer);
        logEntry.setRewardCatalog(catalog);
        logEntry.setRewardType(rewardType);
        logEntry.setRewardName(rewardName);
        logEntry.setRewardValue(rewardValue);
        logEntry.setDispatchStatus("DISPATCHED");
        logEntry.setDispatchChannel(actualChannel);
        logEntry.setDispatchedAt(ZonedDateTime.now());
        logEntry.setNotes("Dispatched via " + actualChannel + " as part of retention workflow.");

        RewardDispatchLog saved = rewardDispatchLogRepository.save(logEntry);

        // Mark the customer's reward as dispatched
        customer.setRewardRedeemed(false); // not yet redeemed, but dispatched
        customerRepository.save(customer);

        log.info("🎁 [REWARD DISPATCH] '{}' ({}) dispatched to {} via {}",
                rewardName, rewardType, customer.getName(), logEntry.getDispatchChannel());

        return toDispatchDTO(saved);
    }

    /**
     * Marks a previously dispatched reward as redeemed.
     */
    @Transactional
    public RewardDispatchLogDTO markRedeemed(UUID dispatchLogId) {
        RewardDispatchLog entry = rewardDispatchLogRepository.findById(dispatchLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch log not found: " + dispatchLogId));

        entry.setDispatchStatus("REDEEMED");
        entry.setRedeemedAt(ZonedDateTime.now());
        rewardDispatchLogRepository.save(entry);

        entry.getCustomer().setRewardRedeemed(true);
        customerRepository.save(entry.getCustomer());

        log.info("✅ [REWARD REDEEMED] '{}' redeemed by {}", entry.getRewardName(), entry.getCustomer().getName());
        return toDispatchDTO(entry);
    }

    // ── Dispatch History ─────────────────────────────────────────────────────

    /**
     * Returns the full reward dispatch history for a given customer.
     */
    public List<RewardDispatchLogDTO> getDispatchHistory(UUID customerId) {
        return rewardDispatchLogRepository
                .findByCustomerIdOrderByDispatchedAtDesc(customerId)
                .stream()
                .map(this::toDispatchDTO)
                .collect(Collectors.toList());
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private RewardCatalogDTO toCatalogDTO(RewardCatalog r) {
        RewardCatalogDTO dto = new RewardCatalogDTO();
        dto.setId(r.getId());
        dto.setCompanyId(r.getCompany() != null ? r.getCompany().getId() : null);
        dto.setRewardType(r.getRewardType());
        dto.setRewardName(r.getRewardName());
        dto.setRewardValue(r.getRewardValue());
        dto.setTargetSegment(r.getTargetSegment());
        dto.setMinFrustrationScore(r.getMinFrustrationScore());
        dto.setRequiresApproval(r.getRequiresApproval());
        dto.setActive(r.getActive());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }

    private RewardDispatchLogDTO toDispatchDTO(RewardDispatchLog r) {
        RewardDispatchLogDTO dto = new RewardDispatchLogDTO();
        dto.setId(r.getId());
        dto.setCustomerId(r.getCustomer().getId());
        dto.setCustomerName(r.getCustomer().getName());
        dto.setRewardCatalogId(r.getRewardCatalog() != null ? r.getRewardCatalog().getId() : null);
        dto.setRewardType(r.getRewardType());
        dto.setRewardName(r.getRewardName());
        dto.setRewardValue(r.getRewardValue());
        dto.setDispatchStatus(r.getDispatchStatus());
        dto.setDispatchChannel(r.getDispatchChannel());
        dto.setDispatchedAt(r.getDispatchedAt());
        dto.setRedeemedAt(r.getRedeemedAt());
        dto.setNotes(r.getNotes());
        return dto;
    }
}
