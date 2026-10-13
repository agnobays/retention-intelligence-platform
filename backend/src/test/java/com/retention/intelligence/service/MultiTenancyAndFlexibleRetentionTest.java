package com.retention.intelligence.service;

import com.retention.intelligence.dto.RecoveryDTO;
import com.retention.intelligence.entity.Company;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.entity.CustomerValueScore;
import com.retention.intelligence.entity.RecoveryPlan;
import com.retention.intelligence.entity.RetentionPolicy;
import com.retention.intelligence.multitenancy.TenantContext;
import com.retention.intelligence.repository.CustomerRepository;
import com.retention.intelligence.repository.CustomerValueScoreRepository;
import com.retention.intelligence.repository.RecoveryPlanRepository;
import com.retention.intelligence.repository.RetentionPolicyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MultiTenancyAndFlexibleRetentionTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerValueScoreRepository customerValueScoreRepository;

    @Mock
    private RecoveryPlanRepository recoveryPlanRepository;

    @Mock
    private RetentionPolicyRepository retentionPolicyRepository;

    @Mock
    private RewardService rewardService;

    @Mock
    private AiAgentService aiAgentService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private DecisionEngineService decisionEngineService;

    private Company bankCompany;
    private Company logisticsCompany;
    private Customer bankCustomer;
    private Customer logisticsCustomer;

    @BeforeEach
    void setUp() {
        bankCompany = Company.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("Standard Bank CIB")
                .domain("standardbank.co.za")
                .industry("FINANCIAL_SERVICES")
                .build();

        logisticsCompany = Company.builder()
                .id(UUID.fromString("88888888-8888-8888-8888-888888888888"))
                .name("Kasi Express Logistics")
                .domain("kasiexpress.co.za")
                .industry("LOGISTICS_COLD_CHAIN")
                .build();

        bankCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .company(bankCompany)
                .externalCustomerId("SB-CIB-1001")
                .name("Shoprite Holdings Ltd")
                .arr(new BigDecimal("3000000.00"))
                .frustrationScore(85)
                .customerSegment("CORPORATE_CIB")
                .build();

        logisticsCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .company(logisticsCompany)
                .externalCustomerId("KEL-FLEET-301")
                .name("Soweto Fresh Wholesalers")
                .arr(new BigDecimal("2160000.00"))
                .frustrationScore(82)
                .customerSegment("ENTERPRISE_FLEET")
                .build();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void testTenantContextIsolation() {
        TenantContext.setTenantId(bankCompany.getId().toString());
        assertEquals(bankCompany.getId().toString(), TenantContext.getTenantId());

        TenantContext.setTenantId(logisticsCompany.getId().toString());
        assertEquals(logisticsCompany.getId().toString(), TenantContext.getTenantId());

        TenantContext.clear();
        assertEquals(TenantContext.DEFAULT_TENANT, TenantContext.getTenantId());
    }

    @Test
    void testBankingRetentionPolicyDecision() {
        RetentionPolicy bankPolicy = RetentionPolicy.builder()
                .company(bankCompany)
                .businessType("FINANCIAL_SERVICES")
                .brandName("Standard Bank Executive Care Desk")
                .senderName("Sipho Dlamini")
                .autoApprovalThreshold(70)
                .build();

        when(customerRepository.findById(bankCustomer.getId())).thenReturn(Optional.of(bankCustomer));
        when(retentionPolicyRepository.findByCompanyId(bankCompany.getId())).thenReturn(Optional.of(bankPolicy));
        when(customerValueScoreRepository.findByCustomerId(bankCustomer.getId())).thenReturn(Optional.of(
                CustomerValueScore.builder().strategicValueTier("TIER_1").build()
        ));
        when(recoveryPlanRepository.save(any(RecoveryPlan.class))).thenAnswer(i -> {
            RecoveryPlan p = i.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        RecoveryDTO result = decisionEngineService.recommendRecoveryAction(bankCustomer.getId());

        assertNotNull(result);
        assertEquals("SENIOR_RELATIONSHIP_MANAGER_INTERVENTION_AND_PREMIUM_LOYALTY_REWARD", result.getRecommendedAction());
        assertEquals(15, result.getDiscountPercentage());
        assertTrue(result.getRequiresApproval());
    }

    @Test
    void testLocalBusinessLogisticsRetentionPolicyDecision() {
        RetentionPolicy logisticsPolicy = RetentionPolicy.builder()
                .company(logisticsCompany)
                .businessType("LOGISTICS_COLD_CHAIN")
                .brandName("Kasi Express Fleet Care")
                .senderName("Lerato Khumalo")
                .autoApprovalThreshold(65)
                .build();

        when(customerRepository.findById(logisticsCustomer.getId())).thenReturn(Optional.of(logisticsCustomer));
        when(retentionPolicyRepository.findByCompanyId(logisticsCompany.getId())).thenReturn(Optional.of(logisticsPolicy));
        when(customerValueScoreRepository.findByCustomerId(logisticsCustomer.getId())).thenReturn(Optional.of(
                CustomerValueScore.builder().strategicValueTier("TIER_1").build()
        ));
        when(recoveryPlanRepository.save(any(RecoveryPlan.class))).thenAnswer(i -> {
            RecoveryPlan p = i.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        RecoveryDTO result = decisionEngineService.recommendRecoveryAction(logisticsCustomer.getId());

        assertNotNull(result);
        assertEquals("FLEET_OPERATIONS_DIRECTOR_INTERVENTION_AND_FUEL_REBATE", result.getRecommendedAction());
        assertEquals(15, result.getDiscountPercentage());
        assertTrue(result.getRequiresApproval());
    }
}
