package com.fitness_application.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness_application.user.dto.ApiResponse;
import com.fitness_application.user.dto.UserProfileDto;
import com.fitness_application.user.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "User profile management APIs")
@Slf4j
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    @GetMapping("/profile")
    @Operation(summary = "Get user profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Fetching profile for user: {}", userId);
        ApiResponse<UserProfileDto> response = userProfileService.getProfile(userId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/profile")
    @Operation(summary = "Create user profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> createProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfileDto profileDto) {
        log.info("Creating profile for user: {}", userId);
        ApiResponse<UserProfileDto> response = userProfileService.createProfile(userId, profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/profile")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfileDto profileDto) {
        log.info("Updating profile for user: {}", userId);
        ApiResponse<UserProfileDto> response = userProfileService.updateProfile(userId, profileDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get user profile by ID (Admin or internal service call)")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfileById(@PathVariable Long profileId) {
        log.info("Fetching profile by ID: {}", profileId);
        ApiResponse<UserProfileDto> response = userProfileService.getProfileById(profileId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/profile")
    @Operation(summary = "Delete user profile")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Deleting profile for user: {}", userId);
        ApiResponse<Void> response = userProfileService.deleteProfile(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/profile/bmi")
    @Operation(summary = "Calculate BMI")
    public ResponseEntity<ApiResponse<Double>> calculateBMI(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Calculating BMI for user: {}", userId);
        ApiResponse<Double> response = userProfileService.calculateBMI(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/profile/bmr")
    @Operation(summary = "Calculate BMR")
    public ResponseEntity<ApiResponse<Double>> calculateBMR(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Calculating BMR for user: {}", userId);
        ApiResponse<Double> response = userProfileService.calculateBMR(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Service health check")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is healthy");
    }
}
