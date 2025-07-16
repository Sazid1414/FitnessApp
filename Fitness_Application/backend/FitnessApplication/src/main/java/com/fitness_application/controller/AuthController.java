package com.fitness_application.controller;

import com.fitness_application.dto.*;
import com.fitness_application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

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
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        ApiResponse<UserDto> response = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/profile")
    @Operation(summary = "Update user profile")
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
}
