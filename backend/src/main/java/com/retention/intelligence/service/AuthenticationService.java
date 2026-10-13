package com.retention.intelligence.service;

import com.retention.intelligence.dto.AuthDTO;
import com.retention.intelligence.entity.Company;
import com.retention.intelligence.entity.User;
import com.retention.intelligence.exception.UnauthorizedAccessException;
import com.retention.intelligence.repository.UserRepository;
import com.retention.intelligence.security.JwtTokenProvider;
import com.retention.intelligence.security.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedAccessException("Invalid email or password.");
        }

        Role userRole = user.getRole() != null ? user.getRole() : Role.ANALYST;
        Company company = user.getCompany();
        UUID companyId = company != null ? company.getId() : UUID.fromString("11111111-1111-1111-1111-111111111111");
        String companyName = company != null ? company.getName() : "Enterprise";
        String companyDomain = company != null ? company.getDomain() : "enterprise.com";
        String industry = company != null && company.getIndustry() != null ? company.getIndustry() : "GENERAL";

        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), userRole, companyId);

        return AuthDTO.AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(userRole)
                .companyId(companyId)
                .companyName(companyName)
                .companyDomain(companyDomain)
                .industry(industry)
                .build();
    }
}
