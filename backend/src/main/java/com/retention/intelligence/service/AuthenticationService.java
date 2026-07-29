package com.retention.intelligence.service;

import com.retention.intelligence.dto.AuthDTO;
import com.retention.intelligence.security.JwtTokenProvider;
import com.retention.intelligence.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtTokenProvider tokenProvider;

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        // Skeleton logic: Generate JWT token for user login authentication
        UUID dummyUserId = UUID.randomUUID();
        UUID dummyCompanyId = UUID.randomUUID();
        String token = tokenProvider.generateToken(dummyUserId, request.getEmail(), Role.MANAGER, dummyCompanyId);

        return AuthDTO.AuthResponse.builder()
                .token(token)
                .userId(dummyUserId)
                .email(request.getEmail())
                .role(Role.MANAGER)
                .companyId(dummyCompanyId)
                .build();
    }
}
