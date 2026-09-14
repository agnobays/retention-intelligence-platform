package com.retention.intelligence.service;

import com.retention.intelligence.dto.RecoveryDTO;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.entity.CustomerValueScore;
import com.retention.intelligence.entity.RecoveryPlan;
import com.retention.intelligence.exception.ResourceNotFoundException;
import com.retention.intelligence.repository.CustomerRepository;
import com.retention.intelligence.repository.CustomerValueScoreRepository;
import com.retention.intelligence.repository.RecoveryPlanRepository;
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

    public RecoveryDTO recommendRecoveryAction(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        Optional<CustomerValueScore> valueScoreOpt = customerValueScoreRepository.findByCustomerId(customerId);

        String strategicTier = valueScoreOpt.map(CustomerValueScore::getStrategicValueTier).orElse("TIER_2");
        BigDecimal arr = customer.getArr() != null ? customer.getArr() : BigDecimal.ZERO;
        String segment = customer.getCustomerSegment() != null ? customer.getCustomerSegment() : "COMMERCIAL_SME";
        int frustration = customer.getFrustrationScore() != null ? customer.getFrustrationScore() : 75;

        String recommendedAction;
        int discountPercentage;
        boolean requiresApproval;
        String rewardCategory;
        String rewardValue;

        if ("PRIVATE_CLIENT".equalsIgnoreCase(segment) || frustration >= 80 || "TIER_1".equals(strategicTier) || arr.compareTo(new BigDecimal("2000000.00")) >= 0) {
            recommendedAction = "SENIOR_RELATIONSHIP_MANAGER_INTERVENTION_AND_PREMIUM_LOYALTY_REWARD";
            discountPercentage = 15;
            requiresApproval = true;
            rewardCategory = "Lifestyle & Executive Experience";
            rewardValue = "R1,500 Lifestyle Experience Voucher & Dedicated Private Banker";
        } else if ("COMMERCIAL_SME".equalsIgnoreCase(segment) || frustration >= 70 || "TIER_2".equals(strategicTier) || arr.compareTo(new BigDecimal("500000.00")) >= 0) {
            recommendedAction = "BUSINESS_SPECIALIST_INTERVENTION_AND_MERCHANT_FEE_WAIVER";
            discountPercentage = 10;
            requiresApproval = true;
            rewardCategory = "Merchant Fee Credit & Business Benefit";
            rewardValue = "Merchant Fee Waiver & Priority Settlement Desk Access";
        } else {
            recommendedAction = "AUTOMATED_SERVICE_RECOVERY_AND_RETAIL_LOYALTY_REWARD";
            discountPercentage = 5;
            requiresApproval = false;
            rewardCategory = "Retail Voucher & Loyalty Points";
            rewardValue = "5,000 Loyalty Points / R250 Retail Voucher";
        }

        // Update Customer Entity with decision outputs
        customer.setRecommendedIntervention(recommendedAction);
        customer.setRewardCategory(rewardCategory);
        customer.setRewardValue(rewardValue);
        customerRepository.save(customer);

        RecoveryPlan plan = RecoveryPlan.builder()
                .customer(customer)
                .recommendedAction(recommendedAction)
                .discountPercentage(discountPercentage)
                .status(requiresApproval ? "PENDING_APPROVAL" : "APPROVED")
                .outcomeNotes(requiresApproval ? "Awaiting Senior Management / RM approval for " + rewardCategory + "." : "Auto-approved loyalty recovery action.")
                .build();

        RecoveryPlan saved = recoveryPlanRepository.save(plan);

        log.info("🧠 [SANISA DECISION ENGINE] Segment={}, Frustration={}/100 -> Recommended Strategy for {}: Action={}, Reward='{}' (Approval Required={})",
                segment, frustration, customer.getName(), recommendedAction, rewardValue, requiresApproval);

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
