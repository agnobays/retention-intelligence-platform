package com.retention.intelligence.workflow.delegates;

import com.retention.intelligence.service.RewardService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Camunda delegate that fires AFTER manager approval.
 * Dispatches the configured loyalty reward (points, cashback, voucher, fee waiver, etc.)
 * to the customer and logs the dispatch.
 */
@Component("dispatchRewardDelegate")
public class DispatchRewardDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(DispatchRewardDelegate.class);

    private final RewardService rewardService;

    public DispatchRewardDelegate(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String customerIdStr  = (String) execution.getVariable("customerId");
        String rewardCatalogIdStr = (String) execution.getVariable("rewardCatalogId");  // optional

        log.info("================================================================================");
        log.info("🎁 [CAMUNDA DELEGATE] DISPATCH REWARD");
        log.info("Process Instance ID  : {}", execution.getProcessInstanceId());
        log.info("Customer ID          : {}", customerIdStr);
        log.info("Reward Catalog ID    : {}", rewardCatalogIdStr);

        if (customerIdStr != null) {
            UUID customerId     = UUID.fromString(customerIdStr);
            UUID rewardCatalogId = rewardCatalogIdStr != null ? UUID.fromString(rewardCatalogIdStr) : null;

            var dispatched = rewardService.dispatchReward(customerId, rewardCatalogId, "EMAIL");

            execution.setVariable("rewardDispatched", true);
            execution.setVariable("dispatchedRewardName",  dispatched.getRewardName());
            execution.setVariable("dispatchedRewardValue", dispatched.getRewardValue());
            execution.setVariable("dispatchedRewardType",  dispatched.getRewardType());
            execution.setVariable("dispatchLogId",         dispatched.getId().toString());

            log.info("Reward Dispatched    : {}", dispatched.getRewardName());
            log.info("Reward Value         : {}", dispatched.getRewardValue());
            log.info("Reward Type          : {}", dispatched.getRewardType());
            log.info("Dispatch Channel     : {}", dispatched.getDispatchChannel());
            log.info("Dispatch Log ID      : {}", dispatched.getId());
        } else {
            execution.setVariable("rewardDispatched", false);
            log.warn("⚠️ No Customer ID found in process context. Reward dispatch skipped.");
        }

        log.info("================================================================================");
    }
}
