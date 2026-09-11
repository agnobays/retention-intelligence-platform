package com.retention.intelligence.controller;

import com.retention.intelligence.entity.User;
import com.retention.intelligence.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/users", "/users"})
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing user accounts and roles")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get Company Users", description = "Returns user list by tenant company ID")
    public ResponseEntity<List<User>> getUsersByCompany(@PathVariable UUID companyId) {
        log.info("👤 API REQUEST: GET /api/v1/users/company/{}", companyId);
        return ResponseEntity.ok(userService.getUsersByCompany(companyId));
    }
}
