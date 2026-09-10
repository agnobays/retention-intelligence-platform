package com.retention.intelligence.workflow.delegates;

import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.repository.CustomerRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("closeRecoveryCaseDelegate")
public class CloseRecoveryCaseDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CloseRecoveryCaseDelegate.class);

    private final CustomerRepository customerRepository;

    public CloseRecoveryCaseDelegate(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing Camunda Delegate: Close Recovery Case for process instance {}", execution.getProcessInstanceId());
        String customerIdStr = (String) execution.getVariable("customerId");
        if (customerIdStr != null) {
            UUID customerId = UUID.fromString(customerIdStr);
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if (customer != null) {
                customer.setStatus("SAVED");
                customer.setHealthScore(85);
                customerRepository.save(customer);
            }
        }
    }
}
