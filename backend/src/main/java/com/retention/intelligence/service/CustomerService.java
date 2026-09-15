package com.retention.intelligence.service;

import com.retention.intelligence.dto.BatchImportResultDTO;
import com.retention.intelligence.dto.CustomerDTO;
import com.retention.intelligence.dto.WorkflowDTO;
import com.retention.intelligence.entity.Company;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.repository.CompanyRepository;
import com.retention.intelligence.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final DetectionEngineService detectionEngineService;
    private final CustomerValueEngineService customerValueEngineService;
    private final DecisionEngineService decisionEngineService;
    private final WorkflowService workflowService;
    private final NotificationService notificationService;

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CustomerDTO> getCustomersByCompany(UUID companyId) {
        List<Customer> list = customerRepository.findByCompanyId(companyId);
        if (list.isEmpty()) {
            list = customerRepository.findAll();
        }
        return list.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO importCustomer(CustomerDTO dto) {
        Company defaultCompany = null;
        if (dto.getCompanyId() != null) {
            defaultCompany = companyRepository.findById(dto.getCompanyId()).orElse(null);
        }
        if (defaultCompany == null) {
            defaultCompany = companyRepository.findById(UUID.fromString("11111111-1111-1111-1111-111111111111")).orElse(null);
        }
        if (defaultCompany == null) {
            defaultCompany = companyRepository.findAll().stream().findFirst().orElse(null);
        }
        if (defaultCompany == null) {
            Company newComp = new Company();
            newComp.setName("Standard Bank CIB");
            newComp.setDomain("standardbank.co.za");
            newComp.setIndustry("FINANCIAL_SERVICES");
            newComp.setSubscriptionTier("ENTERPRISE");
            defaultCompany = companyRepository.save(newComp);
        }

        final Company targetCompany = defaultCompany;
        String extId = (dto.getExternalCustomerId() != null && !dto.getExternalCustomerId().trim().isEmpty())
                ? dto.getExternalCustomerId().trim()
                : "SB-CIB-" + (System.currentTimeMillis() % 10000);

        Customer customer = customerRepository.findByExternalCustomerId(extId)
                .orElseGet(() -> {
                    Customer newCust = new Customer();
                    newCust.setExternalCustomerId(extId);
                    newCust.setCompany(targetCompany);
                    newCust.setName(dto.getName() != null ? dto.getName() : "Unnamed Client");
                    newCust.setEmail(dto.getEmail() != null ? dto.getEmail() : "client@standardbank.co.za");
                    newCust.setMrr(dto.getMrr() != null ? dto.getMrr() : new BigDecimal("250000.00"));
                    newCust.setArr(dto.getArr() != null ? dto.getArr() : new BigDecimal("3000000.00"));
                    newCust.setHealthScore(dto.getHealthScore() != null ? dto.getHealthScore() : 45);
                    newCust.setChurnProbability(dto.getChurnProbability() != null ? dto.getChurnProbability() : new BigDecimal("78.50"));
                    newCust.setStatus(dto.getStatus() != null ? dto.getStatus() : "AT_RISK");
                    return newCust;
                });

        customer.setCompany(targetCompany);

        if (dto.getName() != null) customer.setName(dto.getName());
        if (dto.getEmail() != null) customer.setEmail(dto.getEmail());
        if (dto.getCustomerSegment() != null) customer.setCustomerSegment(dto.getCustomerSegment());
        if (dto.getTenure() != null) customer.setTenure(dto.getTenure());
        if (dto.getProductsHeld() != null) customer.setProductsHeld(dto.getProductsHeld());
        if (dto.getMrr() != null) customer.setMrr(dto.getMrr());
        if (dto.getArr() != null) customer.setArr(dto.getArr());
        if (dto.getHealthScore() != null) customer.setHealthScore(dto.getHealthScore());
        if (dto.getFrustrationScore() != null) customer.setFrustrationScore(dto.getFrustrationScore());
        if (dto.getSatisfactionScore() != null) customer.setSatisfactionScore(dto.getSatisfactionScore());
        if (dto.getChurnProbability() != null) customer.setChurnProbability(dto.getChurnProbability());
        if (dto.getStatus() != null) customer.setStatus(dto.getStatus());
        if (dto.getIssueCategory() != null) customer.setIssueCategory(dto.getIssueCategory());
        if (dto.getIssueSeverity() != null) customer.setIssueSeverity(dto.getIssueSeverity());
        if (dto.getPreviousComplaintsCount() != null) customer.setPreviousComplaintsCount(dto.getPreviousComplaintsCount());
        if (dto.getResolutionTimeHours() != null) customer.setResolutionTimeHours(dto.getResolutionTimeHours());
        if (dto.getInteractionsCount() != null) customer.setInteractionsCount(dto.getInteractionsCount());
        if (dto.getEscalationsCount() != null) customer.setEscalationsCount(dto.getEscalationsCount());
        if (dto.getRecommendedIntervention() != null) customer.setRecommendedIntervention(dto.getRecommendedIntervention());
        if (dto.getRewardCategory() != null) customer.setRewardCategory(dto.getRewardCategory());
        if (dto.getRewardValue() != null) customer.setRewardValue(dto.getRewardValue());
        if (dto.getRewardRedeemed() != null) customer.setRewardRedeemed(dto.getRewardRedeemed());
        if (dto.getPostRecoveryScore() != null) customer.setPostRecoveryScore(dto.getPostRecoveryScore());
        if (dto.getRetentionOutcome() != null) customer.setRetentionOutcome(dto.getRetentionOutcome());

        Customer saved = customerRepository.save(customer);

        // Auto-run AI risk analysis and trigger Camunda Workflow
        triggerPostImportEngines(saved);

        return mapToDTO(saved);
    }

    public BatchImportResultDTO processBatchSpreadsheetImport(List<CustomerDTO> batchList) {
        log.info("================================================================================");
        log.info("📊 [SPREADSHEET BATCH IMPORT] Processing {} corporate account rows", batchList.size());
        log.info("================================================================================");

        int atRiskCount = 0;
        int workflowsLaunched = 0;
        List<BatchImportResultDTO.ProcessedAccountDTO> processedAccounts = new ArrayList<>();

        for (CustomerDTO dto : batchList) {
            CustomerDTO imported = importCustomer(dto);
            Customer customer = customerRepository.findById(imported.getId()).orElse(null);

            double churnProb = imported.getChurnProbability() != null ? imported.getChurnProbability().doubleValue() : 50.0;
            int health = imported.getHealthScore() != null ? imported.getHealthScore() : 50;

            String workflowKey = "None";
            String instanceId = "N/A";

            if (customer != null) {
                if (churnProb >= 70.0 || health < 60) {
                    atRiskCount++;
                    WorkflowDTO wf = workflowService.startRecoveryWorkflow(customer.getId());
                    workflowKey = wf.getProcessDefinitionKey();
                    instanceId = wf.getWorkflowInstanceId();
                    workflowsLaunched++;

                    if (customer.getArr() != null && customer.getArr().compareTo(new BigDecimal("2000000.00")) >= 0) {
                        workflowService.startExecutiveEscalationWorkflow(customer.getId());
                        workflowsLaunched++;
                        workflowKey += " + ExecutiveEscalationProcess";
                    }
                } else {
                    WorkflowDTO wf = workflowService.startChurnPreventionSurveyWorkflow(customer.getId());
                    workflowKey = wf.getProcessDefinitionKey();
                    instanceId = wf.getWorkflowInstanceId();
                    workflowsLaunched++;
                }
            }

            log.info("  ├─ CLIENT ID       : {}", imported.getExternalCustomerId());
            log.info("  ├─ CLIENT DETAILS  : Name='{}', Email='{}', ARR=R{}, HealthScore={}, ChurnProb={}%",
                    imported.getName(), imported.getEmail(), imported.getArr(), health, churnProb);
            log.info("  └─ WORKFLOW/MESSAGE: Launched BPMN Workflow '{}' (Instance ID: {})", workflowKey, instanceId);

            processedAccounts.add(new BatchImportResultDTO.ProcessedAccountDTO(
                    imported.getExternalCustomerId(),
                    imported.getName(),
                    imported.getEmail(),
                    health,
                    churnProb,
                    imported.getStatus(),
                    workflowKey,
                    instanceId
            ));
        }

        if (notificationService != null) {
            notificationService.broadcastNotification(
                "📊 Batch CSV Import Processed",
                "Processed " + batchList.size() + " accounts (" + atRiskCount + " at-risk). Launched " + workflowsLaunched + " Camunda BPMN workflows.",
                "workflow"
            );
        }

        return new BatchImportResultDTO(
                "SUCCESS",
                batchList.size(),
                atRiskCount,
                workflowsLaunched,
                "Successfully analyzed " + batchList.size() + " accounts from spreadsheet and launched " + workflowsLaunched + " Camunda 7 BPMN workflow instances.",
                processedAccounts
        );
    }

    private void triggerPostImportEngines(Customer customer) {
        try {
            detectionEngineService.runDetectionForCustomerInternal(customer.getId());
            customerValueEngineService.calculateCustomerValue(customer.getId());
            decisionEngineService.recommendRecoveryAction(customer.getId());
        } catch (Exception e) {
            log.warn("Non-fatal exception running post-import engines for {}: {}", customer.getName(), e.getMessage());
        }
    }

    private CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .externalCustomerId(customer.getExternalCustomerId())
                .name(customer.getName())
                .email(customer.getEmail())
                .customerSegment(customer.getCustomerSegment())
                .tenure(customer.getTenure())
                .productsHeld(customer.getProductsHeld())
                .mrr(customer.getMrr())
                .arr(customer.getArr())
                .healthScore(customer.getHealthScore())
                .frustrationScore(customer.getFrustrationScore())
                .satisfactionScore(customer.getSatisfactionScore())
                .churnProbability(customer.getChurnProbability())
                .status(customer.getStatus())
                .issueCategory(customer.getIssueCategory())
                .issueSeverity(customer.getIssueSeverity())
                .previousComplaintsCount(customer.getPreviousComplaintsCount())
                .resolutionTimeHours(customer.getResolutionTimeHours())
                .interactionsCount(customer.getInteractionsCount())
                .escalationsCount(customer.getEscalationsCount())
                .recommendedIntervention(customer.getRecommendedIntervention())
                .rewardCategory(customer.getRewardCategory())
                .rewardValue(customer.getRewardValue())
                .rewardRedeemed(customer.getRewardRedeemed())
                .postRecoveryScore(customer.getPostRecoveryScore())
                .retentionOutcome(customer.getRetentionOutcome())
                .contractRenewalDate(customer.getContractRenewalDate())
                .build();
    }
}
