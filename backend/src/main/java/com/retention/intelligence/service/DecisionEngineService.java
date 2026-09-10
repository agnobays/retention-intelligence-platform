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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DecisionEngineService {

    private final CustomerRepository customerRepository;
    private final CustomerValueScoreRepository customerValueScoreRepository;
    private final RecoveryPlanRepository recoveryPlanRepository;

    public RecoveryDTO recommendRecoveryAction(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        Optional<CustomerValueScore> valueScoreOpt = customerValueScoreRepository.findByCustomerId(customerId);

        String strategicTier = valueScoreOpt.map(CustomerValueScore::getStrategicValueTier).orElse("TIER_2");
        BigDecimal arr = customer.getArr() != null ? customer.getArr() : BigDecimal.ZERO;

        String recommendedAction;
        int discountPercentage;
        boolean requiresApproval;

        if ("TIER_1".equals(strategicTier) || arr.compareTo(new BigDecimal("2000000.00")) >= 0) {
            recommendedAction = "DEDICATED_CIB_RELATIONSHIP_MANAGER_OUTREACH_AND_15_PERCENT_FEE_DISCOUNT";
            discountPercentage = 15;
            requiresApproval = true;
        } else if ("TIER_2".equals(strategicTier) || arr.compareTo(new BigDecimal("1000000.00")) >= 0) {
            recommendedAction = "CUSTOM_FX_RATE_LOCK_AND_10_PERCENT_DISCOUNT";
            discountPercentage = 10;
            requiresApproval = true;
        } else {
            recommendedAction = "AUTOMATED_LOAN_RESTRUCTURE_OFFER";
            discountPercentage = 5;
            requiresApproval = false;
        }

        RecoveryPlan plan = RecoveryPlan.builder()
                .customer(customer)
                .recommendedAction(recommendedAction)
                .discountPercentage(discountPercentage)
                .status(requiresApproval ? "PENDING_APPROVAL" : "APPROVED")
                .outcomeNotes(requiresApproval ? "Awaiting Relationship Manager approval." : "Auto-approved system recovery action.")
                .build();

        RecoveryPlan saved = recoveryPlanRepository.save(plan);

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
