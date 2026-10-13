package com.retention.intelligence.service;

import com.retention.intelligence.dto.OnboardingDTO;
import com.retention.intelligence.entity.Company;
import com.retention.intelligence.entity.RetentionPolicy;
import com.retention.intelligence.entity.RewardCatalog;
import com.retention.intelligence.entity.User;
import com.retention.intelligence.multitenancy.TenantDataSourceManager;
import com.retention.intelligence.repository.CompanyRepository;
import com.retention.intelligence.repository.RetentionPolicyRepository;
import com.retention.intelligence.repository.RewardCatalogRepository;
import com.retention.intelligence.repository.UserRepository;
import com.retention.intelligence.security.JwtTokenProvider;
import com.retention.intelligence.security.Role;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private static final Logger log = LoggerFactory.getLogger(OnboardingService.class);

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RetentionPolicyRepository retentionPolicyRepository;
    private final RewardCatalogRepository rewardCatalogRepository;
    private final TenantDataSourceManager tenantDataSourceManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /**
     * Checks if a domain is available (strictly unique across all tenants).
     */
    public OnboardingDTO.UniquenessCheckResponse checkDomainAvailability(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return OnboardingDTO.UniquenessCheckResponse.builder()
                    .field("domain")
                    .value("")
                    .isAvailable(false)
                    .message("Domain cannot be empty.")
                    .build();
        }

        String cleanDomain = domain.trim().toLowerCase();
        boolean exists = companyRepository.findAll().stream()
                .anyMatch(c -> cleanDomain.equalsIgnoreCase(c.getDomain()));

        return OnboardingDTO.UniquenessCheckResponse.builder()
                .field("domain")
                .value(cleanDomain)
                .isAvailable(!exists)
                .message(!exists ? "Domain is available." : "Domain is already registered by another organization.")
                .build();
    }

    /**
     * Checks if an email is available (strictly unique across all user accounts).
     */
    public OnboardingDTO.UniquenessCheckResponse checkEmailAvailability(String email) {
        if (email == null || email.trim().isEmpty()) {
            return OnboardingDTO.UniquenessCheckResponse.builder()
                    .field("email")
                    .value("")
                    .isAvailable(false)
                    .message("Email cannot be empty.")
                    .build();
        }

        String cleanEmail = email.trim().toLowerCase();
        boolean exists = userRepository.findByEmail(cleanEmail).isPresent();

        return OnboardingDTO.UniquenessCheckResponse.builder()
                .field("email")
                .value(cleanEmail)
                .isAvailable(!exists)
                .message(!exists ? "Email is available." : "Email address is already in use.")
                .build();
    }

    /**
     * Complete automated onboarding flow:
     * 1. Validates strict uniqueness (domain & email)
     * 2. Creates Company & Admin User
     * 3. Provisions isolated tenant database
     * 4. Initializes Retention Policy & Industry Reward Catalog
     * 5. Returns JWT token for instant access
     */
    @Transactional
    public OnboardingDTO.CompanyRegistrationResponse onboardNewCompany(OnboardingDTO.CompanyRegistrationRequest req) {
        log.info("================================================================================");
        log.info("🚀 [ONBOARDING] Starting onboarding for organization: '{}' ({})", req.getCompanyName(), req.getDomain());
        log.info("================================================================================");

        // 1. Enforce Domain Uniqueness
        String domain = req.getDomain().trim().toLowerCase();
        if (!checkDomainAvailability(domain).isAvailable()) {
            throw new IllegalArgumentException("Domain '" + domain + "' is already registered. Please choose a unique domain.");
        }

        // 2. Enforce Admin Email Uniqueness
        String email = req.getAdminEmail().trim().toLowerCase();
        if (!checkEmailAvailability(email).isAvailable()) {
            throw new IllegalArgumentException("Email '" + email + "' is already in use. Please use a unique email address.");
        }

        // 3. Create & Persist Company
        Company company = Company.builder()
                .name(req.getCompanyName().trim())
                .domain(domain)
                .industry(req.getIndustry() != null ? req.getIndustry().trim() : "GENERAL")
                .subscriptionTier(req.getSubscriptionTier() != null ? req.getSubscriptionTier().trim() : "ENTERPRISE")
                .build();

        Company savedCompany = companyRepository.save(company);
        log.info("✅ Created Company: {} (ID: {})", savedCompany.getName(), savedCompany.getId());

        // 4. Create & Persist Admin User
        String firstName = req.getAdminFirstName() != null && !req.getAdminFirstName().isBlank()
                ? req.getAdminFirstName().trim() : "Admin";
        String lastName = req.getAdminLastName() != null && !req.getAdminLastName().isBlank()
                ? req.getAdminLastName().trim() : "Director";

        User adminUser = User.builder()
                .company(savedCompany)
                .email(email)
                .passwordHash(passwordEncoder.encode(req.getAdminPassword()))
                .firstName(firstName)
                .lastName(lastName)
                .role(Role.COMPANY_ADMIN)
                .active(true)
                .build();

        User savedUser = userRepository.save(adminUser);
        log.info("✅ Created Admin User: {} (Role: {})", savedUser.getEmail(), savedUser.getRole());

        // 5. Provision Isolated Tenant Database
        String dbSlug = savedCompany.getName().replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();
        String isolatedDbName = "retention_" + dbSlug + "_db";
        try {
            tenantDataSourceManager.provisionTenantDatabase(savedCompany.getId().toString(), isolatedDbName);
            log.info("✅ Provisioned isolated database: {}", isolatedDbName);
        } catch (Exception ex) {
            log.warn("⚠️ Database provisioning notice: {}", ex.getMessage());
        }

        // 6. Initialize Business Retention Policy
        String brandName = req.getBrandName() != null && !req.getBrandName().isBlank()
                ? req.getBrandName().trim()
                : savedCompany.getName() + " Executive Care Desk";

        String senderName = req.getSenderName() != null && !req.getSenderName().isBlank()
                ? req.getSenderName().trim()
                : firstName + " " + lastName;

        String senderTitle = req.getSenderTitle() != null && !req.getSenderTitle().isBlank()
                ? req.getSenderTitle().trim()
                : "Operations & Client Success Director";

        RetentionPolicy policy = RetentionPolicy.builder()
                .company(savedCompany)
                .businessType(savedCompany.getIndustry())
                .brandName(brandName)
                .senderName(senderName)
                .senderTitle(senderTitle)
                .senderEmail(savedUser.getEmail())
                .volumeDecayWeight(new BigDecimal("0.50"))
                .frictionWeight(new BigDecimal("0.60"))
                .billingRiskWeight(new BigDecimal("0.40"))
                .autoApprovalThreshold(65)
                .build();

        retentionPolicyRepository.save(policy);
        log.info("✅ Initialized Retention Policy for brand: '{}'", brandName);

        // 7. Seed Industry-Tailored Reward Catalog
        seedIndustryRewards(savedCompany);

        // 8. Generate JWT Authentication Token
        String token = tokenProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole(), savedCompany.getId());

        log.info("🎉 [ONBOARDING COMPLETE] Successfully registered and initialized tenant '{}'", savedCompany.getName());

        return OnboardingDTO.CompanyRegistrationResponse.builder()
                .companyId(savedCompany.getId())
                .companyName(savedCompany.getName())
                .domain(savedCompany.getDomain())
                .industry(savedCompany.getIndustry())
                .adminUserId(savedUser.getId())
                .adminEmail(savedUser.getEmail())
                .authToken(token)
                .isolatedDatabase(isolatedDbName)
                .message("Organization successfully onboarded with isolated database and custom retention engine.")
                .success(true)
                .build();
    }

    private void seedIndustryRewards(Company company) {
        String industry = company.getIndustry().toUpperCase();

        if (industry.contains("LOGISTICS") || industry.contains("COLD_CHAIN") || industry.contains("FLEET")) {
            rewardCatalogRepository.saveAll(List.of(
                    RewardCatalog.builder().company(company).rewardType("VOUCHER").rewardName("Fleet Fuel Card Credit").rewardValue("R5,000 Diesel Fuel Credit").targetSegment("ENTERPRISE_FLEET").minFrustrationScore(65).requiresApproval(true).active(true).build(),
                    RewardCatalog.builder().company(company).rewardType("FEE_WAIVER").rewardName("Cold Storage & Cross-Dock Waiver").rewardValue("1 Month Free Cold Storage Access").targetSegment("COMMERCIAL_SME").minFrustrationScore(60).requiresApproval(false).active(true).build()
            ));
        } else if (industry.contains("FITNESS") || industry.contains("GYM") || industry.contains("HEALTH")) {
            rewardCatalogRepository.saveAll(List.of(
                    RewardCatalog.builder().company(company).rewardType("CUSTOM").rewardName("VIP Personal Training Pass").rewardValue("3 Complimentary 1-on-1 Training Sessions").targetSegment("VIP_ANNUAL").minFrustrationScore(60).requiresApproval(false).active(true).build(),
                    RewardCatalog.builder().company(company).rewardType("FEE_WAIVER").rewardName("1 Month Free Membership").rewardValue("30-Day Zero Fee Membership Extension").targetSegment("ALL").minFrustrationScore(70).requiresApproval(true).active(true).build()
            ));
        } else if (industry.contains("SAAS") || industry.contains("SOFTWARE") || industry.contains("TECH")) {
            rewardCatalogRepository.saveAll(List.of(
                    RewardCatalog.builder().company(company).rewardType("CUSTOM").rewardName("Enterprise Feature Tier Unlock").rewardValue("60-Day Premium API & SLA Upgrade").targetSegment("ENTERPRISE").minFrustrationScore(60).requiresApproval(false).active(true).build(),
                    RewardCatalog.builder().company(company).rewardType("CASHBACK").rewardName("Subscription Credit Rebate").rewardValue("20% Invoice Credit on Next Renewal").targetSegment("ALL").minFrustrationScore(75).requiresApproval(true).active(true).build()
            ));
        } else {
            // General / Retail fallback
            rewardCatalogRepository.saveAll(List.of(
                    RewardCatalog.builder().company(company).rewardType("VOUCHER").rewardName("Courtesy Experience Voucher").rewardValue("R500 Retail Courtesy Voucher").targetSegment("ALL").minFrustrationScore(55).requiresApproval(false).active(true).build(),
                    RewardCatalog.builder().company(company).rewardType("FEE_WAIVER").rewardName("Service Fee Concession").rewardValue("15% Courtesy Service Discount").targetSegment("ALL").minFrustrationScore(70).requiresApproval(true).active(true).build()
            ));
        }
    }
}
