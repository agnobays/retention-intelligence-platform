package com.retention.intelligence.repository;

import com.retention.intelligence.entity.RecoveryPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecoveryPlanRepository extends JpaRepository<RecoveryPlan, UUID> {
    List<RecoveryPlan> findByCustomerId(UUID customerId);
    List<RecoveryPlan> findByStatus(String status);
}
