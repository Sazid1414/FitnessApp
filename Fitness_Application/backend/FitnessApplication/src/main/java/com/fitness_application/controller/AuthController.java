package com.fitness_application.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness_application.dto.ApiResponse;
import com.fitness_application.dto.JwtResponse;
import com.fitness_application.dto.LoginDto;
import com.fitness_application.dto.UserDto;
import com.fitness_application.dto.UserRegistrationDto;
import com.fitness_application.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user management APIs")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<JwtResponse>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        ApiResponse<JwtResponse> response = authService.register(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginDto loginDto) {
        ApiResponse<JwtResponse> response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        ApiResponse<UserDto> response = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/profile")
    @Operation(summary = "Update user profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserRegistrationDto updateDto) {
        ApiResponse<UserDto> response = authService.updateProfile(userDetails.getUsername(), updateDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/oauth2/authorization/{provider}")
    @Operation(summary = "Initiate OAuth2 login with provider")
    public ResponseEntity<Map<String, String>> oauth2Login(@PathVariable String provider) {
        String authUrl = "/oauth2/authorization/" + provider;
        return ResponseEntity.ok(Map.of("authUrl", authUrl));
    }
    
    @PostMapping("/admin/create")
    @Operation(summary = "Create admin user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createAdminUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        ApiResponse<UserDto> response = authService.createAdminUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
