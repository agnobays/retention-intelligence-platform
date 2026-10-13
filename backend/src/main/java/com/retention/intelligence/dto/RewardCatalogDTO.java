package com.retention.intelligence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.ZonedDateTime;
import java.util.UUID;

public class RewardCatalogDTO {

    private UUID id;
    private UUID companyId;

    @NotBlank(message = "Reward type is required")
    private String rewardType;

    @NotBlank(message = "Reward name is required")
    private String rewardName;

    @NotBlank(message = "Reward value is required")
    private String rewardValue;

    @NotBlank(message = "Target segment is required")
    private String targetSegment;

    @NotNull(message = "Minimum frustration score is required")
    @Min(value = 0, message = "Frustration score must be positive")
    private Integer minFrustrationScore;

    @NotNull(message = "Requires approval flag is required")
    private Boolean requiresApproval;

    private Boolean active;
    private ZonedDateTime createdAt;

    public RewardCatalogDTO() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public String getRewardType() { return rewardType; }
    public void setRewardType(String rewardType) { this.rewardType = rewardType; }

    public String getRewardName() { return rewardName; }
    public void setRewardName(String rewardName) { this.rewardName = rewardName; }

    public String getRewardValue() { return rewardValue; }
    public void setRewardValue(String rewardValue) { this.rewardValue = rewardValue; }

    public String getTargetSegment() { return targetSegment; }
    public void setTargetSegment(String targetSegment) { this.targetSegment = targetSegment; }

    public Integer getMinFrustrationScore() { return minFrustrationScore; }
    public void setMinFrustrationScore(Integer minFrustrationScore) { this.minFrustrationScore = minFrustrationScore; }

    public Boolean getRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(Boolean requiresApproval) { this.requiresApproval = requiresApproval; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
