package com.retention.intelligence.repository;

import com.retention.intelligence.entity.RewardCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardCatalogRepository extends JpaRepository<RewardCatalog, UUID> {

    List<RewardCatalog> findByCompanyIdAndActiveTrueOrderByMinFrustrationScoreDesc(UUID companyId);

    @Query("""
        SELECT r FROM RewardCatalog r
        WHERE r.company.id = :companyId
          AND r.active = TRUE
          AND (r.targetSegment = :segment OR r.targetSegment = 'ALL')
          AND r.minFrustrationScore <= :frustrationScore
        ORDER BY r.minFrustrationScore DESC
    """)
    List<RewardCatalog> findBestMatches(
        @Param("companyId") UUID companyId,
        @Param("segment") String segment,
        @Param("frustrationScore") int frustrationScore
    );
}
