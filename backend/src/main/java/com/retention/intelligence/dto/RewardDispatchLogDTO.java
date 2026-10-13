package com.retention.intelligence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

public class RewardDispatchLogDTO {

    private UUID id;

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    private UUID rewardCatalogId;

    @NotBlank(message = "Reward type is required")
    private String rewardType;

    @NotBlank(message = "Reward name is required")
    private String rewardName;

    @NotBlank(message = "Reward value is required")
    private String rewardValue;

    @NotBlank(message = "Dispatch status is required")
    private String dispatchStatus;

    @NotBlank(message = "Dispatch channel is required")
    private String dispatchChannel;
    private ZonedDateTime dispatchedAt;
    private ZonedDateTime redeemedAt;
    private String notes;

    public RewardDispatchLogDTO() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public UUID getRewardCatalogId() { return rewardCatalogId; }
    public void setRewardCatalogId(UUID rewardCatalogId) { this.rewardCatalogId = rewardCatalogId; }

    public String getRewardType() { return rewardType; }
    public void setRewardType(String rewardType) { this.rewardType = rewardType; }

    public String getRewardName() { return rewardName; }
    public void setRewardName(String rewardName) { this.rewardName = rewardName; }

    public String getRewardValue() { return rewardValue; }
    public void setRewardValue(String rewardValue) { this.rewardValue = rewardValue; }

    public String getDispatchStatus() { return dispatchStatus; }
    public void setDispatchStatus(String dispatchStatus) { this.dispatchStatus = dispatchStatus; }

    public String getDispatchChannel() { return dispatchChannel; }
    public void setDispatchChannel(String dispatchChannel) { this.dispatchChannel = dispatchChannel; }

    public ZonedDateTime getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(ZonedDateTime dispatchedAt) { this.dispatchedAt = dispatchedAt; }

    public ZonedDateTime getRedeemedAt() { return redeemedAt; }
    public void setRedeemedAt(ZonedDateTime redeemedAt) { this.redeemedAt = redeemedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
