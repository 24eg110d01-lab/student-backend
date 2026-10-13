package com.school.controller;

import com.school.dto.AuthRequest;
import com.school.dto.AuthResponse;
import com.school.dto.RegisterRequest;
import com.school.model.User;
import com.school.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication & User Management APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register new user (Admin only)")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
