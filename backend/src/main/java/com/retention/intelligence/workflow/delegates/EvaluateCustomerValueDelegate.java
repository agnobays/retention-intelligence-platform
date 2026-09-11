package com.retention.intelligence.workflow.delegates;

import com.retention.intelligence.dto.CustomerValueDTO;
import com.retention.intelligence.service.CustomerValueEngineService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("evaluateCustomerValueDelegate")
public class EvaluateCustomerValueDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(EvaluateCustomerValueDelegate.class);

    private final CustomerValueEngineService customerValueEngineService;

    public EvaluateCustomerValueDelegate(CustomerValueEngineService customerValueEngineService) {
        this.customerValueEngineService = customerValueEngineService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String customerIdStr = (String) execution.getVariable("customerId");
        log.info("================================================================================");
        log.info("📊 [CAMUNDA DELEGATE] EVALUATE CUSTOMER VALUE");
        log.info("Process Instance ID : {}", execution.getProcessInstanceId());
        log.info("Customer ID         : {}", customerIdStr);

        if (customerIdStr != null) {
            UUID customerId = UUID.fromString(customerIdStr);
            CustomerValueDTO valueDTO = customerValueEngineService.calculateCustomerValue(customerId);
            execution.setVariable("ltvTier", valueDTO.getStrategicValueTier());
            execution.setVariable("slaTier", valueDTO.getSlaTier());

            log.info("Calculated LTV      : R{}", valueDTO.getLtv());
            log.info("Strategic Value Tier: {}", valueDTO.getStrategicValueTier());
            log.info("SLA Tier            : {}", valueDTO.getSlaTier());
        } else {
            execution.setVariable("ltvTier", "TIER_1");
            execution.setVariable("slaTier", "ENTERPRISE_PLATINUM");
            log.info("No Customer ID in context. Defaulted to TIER_1 / ENTERPRISE_PLATINUM.");
        }
        log.info("================================================================================");
    }
}
