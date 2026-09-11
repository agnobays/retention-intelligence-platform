package com.retention.intelligence.service;

import com.retention.intelligence.dto.BatchImportResultDTO;
import com.retention.intelligence.dto.CustomerDTO;
import com.retention.intelligence.dto.WorkflowDTO;
import com.retention.intelligence.entity.Customer;
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
    private final DetectionEngineService detectionEngineService;
    private final CustomerValueEngineService customerValueEngineService;
    private final DecisionEngineService decisionEngineService;
    private final WorkflowService workflowService;

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
        Customer customer = customerRepository.findByExternalCustomerId(dto.getExternalCustomerId())
                .orElseGet(() -> Customer.builder()
                        .externalCustomerId(dto.getExternalCustomerId() != null ? dto.getExternalCustomerId() : "SB-CIB-" + System.currentTimeMillis() % 10000)
                        .name(dto.getName())
                        .email(dto.getEmail())
                        .mrr(dto.getMrr() != null ? dto.getMrr() : new BigDecimal("250000.00"))
                        .arr(dto.getArr() != null ? dto.getArr() : new BigDecimal("3000000.00"))
                        .healthScore(dto.getHealthScore() != null ? dto.getHealthScore() : 45)
                        .churnProbability(dto.getChurnProbability() != null ? dto.getChurnProbability() : new BigDecimal("78.50"))
                        .status(dto.getStatus() != null ? dto.getStatus() : "AT_RISK")
                        .build());

        if (dto.getName() != null) customer.setName(dto.getName());
        if (dto.getEmail() != null) customer.setEmail(dto.getEmail());
        if (dto.getMrr() != null) customer.setMrr(dto.getMrr());
        if (dto.getArr() != null) customer.setArr(dto.getArr());
        if (dto.getHealthScore() != null) customer.setHealthScore(dto.getHealthScore());
        if (dto.getChurnProbability() != null) customer.setChurnProbability(dto.getChurnProbability());
        if (dto.getStatus() != null) customer.setStatus(dto.getStatus());

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

            log.info("✅ Imported Row: Account={}, ExternalID={}, Status={}, ChurnProb={}% -> Launched BPMN: {} ({})",
                    imported.getName(), imported.getExternalCustomerId(), imported.getStatus(), churnProb, workflowKey, instanceId);

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

        log.info("================================================================================");
        log.info("🎉 [SPREADSHEET IMPORT COMPLETE] Imported: {}, At Risk: {}, Workflows Launched: {}",
                batchList.size(), atRiskCount, workflowsLaunched);
        log.info("================================================================================");

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
                .mrr(customer.getMrr())
                .arr(customer.getArr())
                .healthScore(customer.getHealthScore())
                .churnProbability(customer.getChurnProbability())
                .status(customer.getStatus())
                .build();
    }
}
