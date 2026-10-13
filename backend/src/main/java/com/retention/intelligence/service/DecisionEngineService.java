package com.retention.intelligence.service;

import com.retention.intelligence.dto.RecoveryDTO;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.entity.CustomerValueScore;
import com.retention.intelligence.entity.RecoveryPlan;
import com.retention.intelligence.entity.RetentionPolicy;
import com.retention.intelligence.entity.RewardCatalog;
import com.retention.intelligence.exception.ResourceNotFoundException;
import com.retention.intelligence.repository.CustomerRepository;
import com.retention.intelligence.repository.CustomerValueScoreRepository;
import com.retention.intelligence.repository.RecoveryPlanRepository;
import com.retention.intelligence.repository.RetentionPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DecisionEngineService {

    private static final Logger log = LoggerFactory.getLogger(DecisionEngineService.class);

    private final CustomerRepository customerRepository;
    private final CustomerValueScoreRepository customerValueScoreRepository;
    private final RecoveryPlanRepository recoveryPlanRepository;
    private final RetentionPolicyRepository retentionPolicyRepository;
    private final RewardService rewardService;
    private final AiAgentService aiAgentService;
    private final EmailService emailService;

    public RecoveryDTO recommendRecoveryAction(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        Optional<CustomerValueScore> valueScoreOpt = customerValueScoreRepository.findByCustomerId(customerId);
        UUID companyId = customer.getCompany().getId();

        Optional<RetentionPolicy> policyOpt = retentionPolicyRepository.findByCompanyId(companyId);
        RetentionPolicy policy = policyOpt.orElse(null);

        String strategicTier = valueScoreOpt.map(CustomerValueScore::getStrategicValueTier).orElse("TIER_2");
        BigDecimal arr = customer.getArr() != null ? customer.getArr() : BigDecimal.ZERO;
        String segment = customer.getCustomerSegment() != null ? customer.getCustomerSegment() : "COMMERCIAL_SME";
        int frustration = customer.getFrustrationScore() != null ? customer.getFrustrationScore() : 75;

        int approvalThreshold = policy != null ? policy.getAutoApprovalThreshold() : 65;
        String businessType = policy != null ? policy.getBusinessType() : "GENERAL";

        // ── Determine action & discount tier flexibly based on business type & tier ─────
        String recommendedAction;
        int discountPercentage;
        boolean requiresApproval;

        if (frustration >= 80 || "TIER_1".equals(strategicTier) || arr.compareTo(new BigDecimal("1500000.00")) >= 0) {
            discountPercentage = 15;
            requiresApproval = true;
            if ("LOGISTICS_COLD_CHAIN".equalsIgnoreCase(businessType)) {
                recommendedAction = "FLEET_OPERATIONS_DIRECTOR_INTERVENTION_AND_FUEL_REBATE";
            } else if ("FINANCIAL_SERVICES".equalsIgnoreCase(businessType)) {
                recommendedAction = "SENIOR_RELATIONSHIP_MANAGER_INTERVENTION_AND_PREMIUM_LOYALTY_REWARD";
            } else {
                recommendedAction = "EXECUTIVE_DIRECTOR_INTERVENTION_AND_PREMIUM_RETENTION_CONCESSION";
            }
        } else if (frustration >= approvalThreshold || "TIER_2".equals(strategicTier) || arr.compareTo(new BigDecimal("400000.00")) >= 0) {
            discountPercentage = 10;
            requiresApproval = frustration >= approvalThreshold;
            if ("LOGISTICS_COLD_CHAIN".equalsIgnoreCase(businessType)) {
                recommendedAction = "OPERATIONS_SPECIALIST_OUTREACH_AND_BAY_PRIORITY_PASS";
            } else if ("FINANCIAL_SERVICES".equalsIgnoreCase(businessType)) {
                recommendedAction = "BUSINESS_SPECIALIST_INTERVENTION_AND_LOYALTY_REWARD";
            } else {
                recommendedAction = "CUSTOMER_SUCCESS_SPECIALIST_OUTREACH_AND_SERVICE_CREDIT";
            }
        } else {
            discountPercentage = 5;
            requiresApproval = false;
            recommendedAction = "AUTOMATED_SERVICE_RECOVERY_AND_LOYALTY_REWARD";
        }

        // ── Select reward from the company's configurable catalogue ───────────
        RewardCatalog selectedReward = rewardService.selectBestReward(companyId, segment, frustration);

        String rewardCategory;
        String rewardValue;

        // ── A/B Testing Logic for Rewards ─────────────────────────────────────
        String abGroup = customer.getAbTestGroup();
        if (abGroup == null || abGroup.trim().isEmpty()) {
            abGroup = Math.random() > 0.5 ? "VARIANT_A" : "VARIANT_B";
            customer.setAbTestGroup(abGroup);
        }

        if (selectedReward != null) {
            rewardCategory = selectedReward.getRewardType() + " — " + selectedReward.getRewardName();
            rewardValue = selectedReward.getRewardValue();
            requiresApproval = selectedReward.getRequiresApproval();
        } else {
            // Business-type aware fallback
            if ("LOGISTICS_COLD_CHAIN".equalsIgnoreCase(businessType)) {
                rewardCategory = "Logistics Fleet Concession";
                rewardValue = "VARIANT_A".equals(abGroup)
                        ? "R5,000 Shell Fleet Fuel Rebate & Priority Loading"
                        : "1-Month Free Cold Storage Cross-Docking Access";
            } else if ("FINANCIAL_SERVICES".equalsIgnoreCase(businessType)) {
                rewardCategory = "Banking Concession";
                rewardValue = "VARIANT_A".equals(abGroup)
                        ? "R1,500 Lifestyle Experience Voucher & Dedicated Private Banker"
                        : "15% Transaction Fee Concession & Dedicated RM";
            } else {
                rewardCategory = "Customer Courtesy Credit";
                rewardValue = "VARIANT_A".equals(abGroup)
                        ? "10% Subscription Rebate for 3 Months"
                        : "R500 Retail Courtesy Voucher";
            }
        }

        // ── Persist decision on customer entity ───────────────────────────────
        customer.setRecommendedIntervention(recommendedAction);
        customer.setRewardCategory(rewardCategory);
        customer.setRewardValue(rewardValue);
        customerRepository.save(customer);

        RecoveryPlan plan = RecoveryPlan.builder()
                .customer(customer)
                .recommendedAction(recommendedAction)
                .discountPercentage(discountPercentage)
                .status(requiresApproval ? "PENDING_APPROVAL" : "APPROVED")
                .outcomeNotes(requiresApproval
                        ? "Awaiting Management / RM approval for " + rewardCategory + "."
                        : "Auto-approved retention recovery action.")
                .build();

        RecoveryPlan saved = recoveryPlanRepository.save(plan);

        log.info("🧠 [DECISION ENGINE] Business='{}', Segment={}, Frustration={}/100 → Strategy for {}: Action={}, Reward='{}' (Approval Required={})",
                businessType, segment, frustration, customer.getName(), recommendedAction, rewardValue, requiresApproval);

        // ── Auto-dispatch if no approval required ────────────────────────────
        if (!requiresApproval) {
            UUID catalogId = selectedReward != null ? selectedReward.getId() : null;
            rewardService.dispatchReward(customerId, catalogId, "EMAIL");
            log.info("⚡ [DECISION ENGINE] Auto-dispatched reward '{}' for {}", rewardValue, customer.getName());

            try {
                AiAgentService.CustomEmailResponse emailResp = aiAgentService.generateTailoredCustomerEmail(customer, policy);
                String recipientEmail = customer.getEmail() != null ? customer.getEmail() : "client@enterprise.co.za";
                emailService.sendDirectEmail(
                        customer.getName(),
                        customer.getExternalCustomerId(),
                        discountPercentage,
                        rewardValue,
                        recipientEmail
                );
                log.info("📧 [DECISION ENGINE] Dynamic AI Retention Email auto-dispatched to {}", recipientEmail);
            } catch (Exception e) {
                log.error("❌ [DECISION ENGINE] Failed to dispatch AI email for {}", customer.getName(), e);
            }
        }

        return RecoveryDTO.builder()
                .planId(saved.getId())
                .customerId(customerId)
                .recommendedAction(recommendedAction)
                .discountPercentage(discountPercentage)
                .status(saved.getStatus())
                .requiresApproval(requiresApproval)
                .build();
    }
}
