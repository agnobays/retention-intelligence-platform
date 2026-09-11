package com.retention.intelligence.service;

import com.retention.intelligence.dto.RecoveryDTO;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.entity.Notification;
import com.retention.intelligence.entity.RecoveryPlan;
import com.retention.intelligence.exception.ResourceNotFoundException;
import com.retention.intelligence.repository.NotificationRepository;
import com.retention.intelligence.repository.RecoveryPlanRepository;
import com.retention.intelligence.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecoveryEngineService {

    private static final Logger log = LoggerFactory.getLogger(RecoveryEngineService.class);

    private final RecoveryPlanRepository recoveryPlanRepository;
    private final CustomerRepository customerRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public RecoveryDTO executeRecoveryAction(UUID planId) {
        RecoveryPlan plan = recoveryPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Recovery Plan not found with ID: " + planId));

        plan.setStatus("EXECUTING");
        plan.setOutcomeNotes("Dispatched retention concession campaign to core banking systems & email channel.");
        recoveryPlanRepository.save(plan);

        Customer customer = plan.getCustomer();
        if (customer != null) {
            customer.setStatus("RECOVERING");
            customerRepository.save(customer);

            if (plan.getAssignedManager() != null) {
                Notification notification = Notification.builder()
                        .user(plan.getAssignedManager())
                        .title("Recovery Action Initiated")
                        .message("Action '" + plan.getRecommendedAction() + "' is now executing for " + customer.getName() + ".")
                        .read(false)
                        .build();
                notificationRepository.save(notification);
            }
        }

        String extId = customer != null ? customer.getExternalCustomerId() : "SB-CIB-1001";
        String custName = customer != null ? customer.getName() : "Corporate Client";
        String custEmail = customer != null ? customer.getEmail() : "treasury@client.co.za";
        String notificationMsg = "Action '" + plan.getRecommendedAction() + "' (" + (plan.getDiscountPercentage() != null ? plan.getDiscountPercentage() : 15) + "% concession) initiated for " + custName + ".";

        log.info("================================================================================");
        log.info("🛠️ [RECOVERY ENGINE EXECUTION]");
        log.info("  ├─ CLIENT ID       : {}", extId);
        log.info("  ├─ CLIENT DETAILS  : Name='{}', Email='{}', ARR=R{}", custName, custEmail, customer != null ? customer.getArr() : "N/A");
        log.info("  ├─ RECOVERY PLAN ID: {}", planId);
        log.info("  ├─ ACTION EXECUTED : {}", plan.getRecommendedAction());
        log.info("  └─ MESSAGE DISPATCHED: \"{}\"", notificationMsg);
        log.info("================================================================================");

        // Trigger Resend API email dispatch for Scenario A / Scenario B
        emailService.sendRecoveryEmail(plan);

        return RecoveryDTO.builder()
                .planId(plan.getId())
                .customerId(customer != null ? customer.getId() : null)
                .recommendedAction(plan.getRecommendedAction())
                .discountPercentage(plan.getDiscountPercentage())
                .status(plan.getStatus())
                .workflowInstanceId(plan.getWorkflowInstanceId())
                .build();
    }
}
