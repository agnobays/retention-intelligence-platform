package com.retention.intelligence.service;

import com.retention.intelligence.dto.OnboardingDTO;
import com.retention.intelligence.entity.Company;
import com.retention.intelligence.entity.RetentionPolicy;
import com.retention.intelligence.entity.User;
import com.retention.intelligence.multitenancy.TenantDataSourceManager;
import com.retention.intelligence.repository.CompanyRepository;
import com.retention.intelligence.repository.RetentionPolicyRepository;
import com.retention.intelligence.repository.RewardCatalogRepository;
import com.retention.intelligence.repository.UserRepository;
import com.retention.intelligence.security.JwtTokenProvider;
import com.retention.intelligence.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RetentionPolicyRepository retentionPolicyRepository;

    @Mock
    private RewardCatalogRepository rewardCatalogRepository;

    @Mock
    private TenantDataSourceManager tenantDataSourceManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private OnboardingService onboardingService;

    private Company existingCompany;
    private User existingUser;

    @BeforeEach
    void setUp() {
        existingCompany = Company.builder()
                .id(UUID.randomUUID())
                .name("Standard Bank CIB")
                .domain("standardbank.co.za")
                .industry("FINANCIAL_SERVICES")
                .build();

        existingUser = User.builder()
                .id(UUID.randomUUID())
                .email("admin@standardbank.co.za")
                .role(Role.COMPANY_ADMIN)
                .build();
    }

    @Test
    void testCheckDomainAvailability() {
        when(companyRepository.findAll()).thenReturn(List.of(existingCompany));

        OnboardingDTO.UniquenessCheckResponse taken = onboardingService.checkDomainAvailability("standardbank.co.za");
        assertFalse(taken.isAvailable());

        OnboardingDTO.UniquenessCheckResponse available = onboardingService.checkDomainAvailability("newfintech.co.za");
        assertTrue(available.isAvailable());
    }

    @Test
    void testCheckEmailAvailability() {
        when(userRepository.findByEmail("admin@standardbank.co.za")).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("newadmin@newfintech.co.za")).thenReturn(Optional.empty());

        OnboardingDTO.UniquenessCheckResponse taken = onboardingService.checkEmailAvailability("admin@standardbank.co.za");
        assertFalse(taken.isAvailable());

        OnboardingDTO.UniquenessCheckResponse available = onboardingService.checkEmailAvailability("newadmin@newfintech.co.za");
        assertTrue(available.isAvailable());
    }

    @Test
    void testOnboardNewCompany_DuplicateDomain_ThrowsException() {
        when(companyRepository.findAll()).thenReturn(List.of(existingCompany));

        OnboardingDTO.CompanyRegistrationRequest req = OnboardingDTO.CompanyRegistrationRequest.builder()
                .companyName("Duplicate SB")
                .domain("standardbank.co.za")
                .adminEmail("unique@new.co.za")
                .adminPassword("Secret123!")
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                onboardingService.onboardNewCompany(req)
        );
        assertTrue(ex.getMessage().contains("already registered"));
    }

    @Test
    void testOnboardNewCompany_SuccessFlow() {
        when(companyRepository.findAll()).thenReturn(Collections.emptyList());
        when(userRepository.findByEmail("director@gympro.co.za")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("encoded_pass");

        UUID generatedCompId = UUID.randomUUID();
        UUID generatedUserId = UUID.randomUUID();

        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> {
            Company c = inv.getArgument(0);
            c.setId(generatedCompId);
            return c;
        });

        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(generatedUserId);
            return u;
        });

        when(tokenProvider.generateToken(eq(generatedUserId), eq("director@gympro.co.za"), eq(Role.COMPANY_ADMIN), eq(generatedCompId)))
                .thenReturn("mock_jwt_token");

        OnboardingDTO.CompanyRegistrationRequest req = OnboardingDTO.CompanyRegistrationRequest.builder()
                .companyName("GymPro Fitness SA")
                .domain("gympro.co.za")
                .industry("FITNESS_WELLNESS")
                .adminFirstName("Arnold")
                .adminLastName("Schwarzenegger")
                .adminEmail("director@gympro.co.za")
                .adminPassword("SecurePass123!")
                .brandName("GymPro Member Desk")
                .senderName("Arnold")
                .senderTitle("Managing Director")
                .build();

        OnboardingDTO.CompanyRegistrationResponse resp = onboardingService.onboardNewCompany(req);

        assertNotNull(resp);
        assertTrue(resp.isSuccess());
        assertEquals(generatedCompId, resp.getCompanyId());
        assertEquals("GymPro Fitness SA", resp.getCompanyName());
        assertEquals("gympro.co.za", resp.getDomain());
        assertEquals("retention_gympro_fitness_sa_db", resp.getIsolatedDatabase());
        assertEquals("mock_jwt_token", resp.getAuthToken());

        // Verify isolated database provisioning was triggered
        verify(tenantDataSourceManager, times(1)).provisionTenantDatabase(
                eq(generatedCompId.toString()),
                eq("retention_gympro_fitness_sa_db")
        );

        // Verify policy and reward catalog persistence
        verify(retentionPolicyRepository, times(1)).save(any(RetentionPolicy.class));
        verify(rewardCatalogRepository, times(1)).saveAll(any());
    }
}
