package com.retention.intelligence.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "reward_dispatch_log")
public class RewardDispatchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_catalog_id")
    private RewardCatalog rewardCatalog;

    @Column(name = "reward_type", nullable = false)
    private String rewardType;

    @Column(name = "reward_name", nullable = false)
    private String rewardName;

    @Column(name = "reward_value", nullable = false)
    private String rewardValue;

    @Column(name = "dispatch_status", nullable = false)
    private String dispatchStatus; // DISPATCHED | REDEEMED | EXPIRED | FAILED

    @Column(name = "dispatch_channel", nullable = false)
    private String dispatchChannel; // EMAIL | SMS | API | MANUAL

    @Column(name = "dispatched_at", nullable = false)
    private ZonedDateTime dispatchedAt;

    @Column(name = "redeemed_at")
    private ZonedDateTime redeemedAt;

    @Column(name = "notes")
    private String notes;

    public RewardDispatchLog() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public RewardCatalog getRewardCatalog() { return rewardCatalog; }
    public void setRewardCatalog(RewardCatalog rewardCatalog) { this.rewardCatalog = rewardCatalog; }

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
