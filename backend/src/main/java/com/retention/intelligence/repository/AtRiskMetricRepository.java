package com.retention.intelligence.repository;

import com.retention.intelligence.entity.AtRiskMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AtRiskMetricRepository extends JpaRepository<AtRiskMetric, UUID> {
    List<AtRiskMetric> findByCustomerId(UUID customerId);
}
