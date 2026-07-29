package com.retention.intelligence.controller;

import com.retention.intelligence.entity.User;
import com.retention.intelligence.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing user accounts and roles")
public class UserController {

    private final UserService userService;

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    @Operation(summary = "Get Company Users", description = "Returns user list by tenant company ID")
    public ResponseEntity<List<User>> getUsersByCompany(@PathVariable UUID companyId) {
        return ResponseEntity.ok(userService.getUsersByCompany(companyId));
    }
}
