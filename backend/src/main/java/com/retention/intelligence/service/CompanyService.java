package com.retention.intelligence.service;

import com.retention.intelligence.entity.Company;
import com.retention.intelligence.entity.RetentionPolicy;
import com.retention.intelligence.repository.CompanyRepository;
import com.retention.intelligence.repository.RetentionPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final RetentionPolicyRepository retentionPolicyRepository;

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Optional<Company> getCompanyById(UUID id) {
        return companyRepository.findById(id);
    }

    public Optional<RetentionPolicy> getPolicyForCompany(UUID companyId) {
        return retentionPolicyRepository.findByCompanyId(companyId);
    }

    public RetentionPolicy saveOrUpdatePolicy(UUID companyId, RetentionPolicy policy) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found: " + companyId));

        RetentionPolicy existing = retentionPolicyRepository.findByCompanyId(companyId).orElse(null);
        if (existing != null) {
            existing.setBrandName(policy.getBrandName());
            existing.setBusinessType(policy.getBusinessType());
            existing.setSenderName(policy.getSenderName());
            existing.setSenderTitle(policy.getSenderTitle());
            existing.setSenderEmail(policy.getSenderEmail());
            existing.setVolumeDecayWeight(policy.getVolumeDecayWeight());
            existing.setFrictionWeight(policy.getFrictionWeight());
            existing.setBillingRiskWeight(policy.getBillingRiskWeight());
            existing.setAutoApprovalThreshold(policy.getAutoApprovalThreshold());
            return retentionPolicyRepository.save(existing);
        } else {
            policy.setCompany(company);
            return retentionPolicyRepository.save(policy);
        }
    }
}
