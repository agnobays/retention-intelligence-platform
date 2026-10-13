package com.retention.intelligence.repository;

import com.retention.intelligence.entity.RewardDispatchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardDispatchLogRepository extends JpaRepository<RewardDispatchLog, UUID> {

    List<RewardDispatchLog> findByCustomerIdOrderByDispatchedAtDesc(UUID customerId);
}
