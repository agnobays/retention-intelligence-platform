package com.retention.intelligence.service;

import com.retention.intelligence.dto.CustomerValueDTO;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.entity.CustomerValueScore;
import com.retention.intelligence.exception.ResourceNotFoundException;
import com.retention.intelligence.repository.CustomerRepository;
import com.retention.intelligence.repository.CustomerValueScoreRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerValueEngineService {

    private static final Logger log = LoggerFactory.getLogger(CustomerValueEngineService.class);

    private final CustomerRepository customerRepository;
    private final CustomerValueScoreRepository customerValueScoreRepository;

    public CustomerValueDTO calculateCustomerValue(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        CustomerValueScore score = customerValueScoreRepository.findByCustomerId(customerId)
                .orElseGet(() -> CustomerValueScore.builder()
                        .customer(customer)
                        .usageFrequencyScore(65)
                        .supportTicketVolume(4)
                        .build());

        BigDecimal arr = customer.getArr() != null ? customer.getArr() : new BigDecimal("1000000.00");
        BigDecimal calculatedLtv = arr.multiply(new BigDecimal("4.5"));
        score.setLtv(calculatedLtv);

        String slaTier;
        String strategicTier;

        if (calculatedLtv.compareTo(new BigDecimal("10000000.00")) >= 0) {
            slaTier = "ENTERPRISE_PLATINUM";
            strategicTier = "TIER_1";
        } else if (calculatedLtv.compareTo(new BigDecimal("5000000.00")) >= 0) {
            slaTier = "ENTERPRISE_GOLD";
            strategicTier = "TIER_1";
        } else if (calculatedLtv.compareTo(new BigDecimal("2000000.00")) >= 0) {
            slaTier = "ENTERPRISE_GOLD";
            strategicTier = "TIER_2";
        } else {
            slaTier = "STANDARD";
            strategicTier = "TIER_3";
        }

        score.setSlaTier(slaTier);
        score.setStrategicValueTier(strategicTier);
        customerValueScoreRepository.save(score);

        log.info("💎 [CUSTOMER VALUE ENGINE] Account={}: ARR=R{}, Calculated LTV=R{}, Tier={}, SLA={}",
                customer.getName(), arr, calculatedLtv, strategicTier, slaTier);

        return CustomerValueDTO.builder()
                .customerId(customerId)
                .ltv(calculatedLtv)
                .usageFrequencyScore(score.getUsageFrequencyScore())
                .supportTicketVolume(score.getSupportTicketVolume())
                .slaTier(slaTier)
                .strategicValueTier(strategicTier)
                .build();
    }
}
