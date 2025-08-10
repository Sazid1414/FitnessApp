package com.fitness_application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitness_application.dto.ApiResponse;
import com.fitness_application.dto.UserDto;
import com.fitness_application.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin-only operations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/users")
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse<List<UserDto>> response = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user by ID (Admin only)")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long userId) {
        ApiResponse<UserDto> response = adminService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/users/{userId}/role")
    @Operation(summary = "Update user role (Admin only)")
    public ResponseEntity<ApiResponse<UserDto>> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        ApiResponse<UserDto> response = adminService.updateUserRole(userId, role);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/users/{userId}/status")
    @Operation(summary = "Enable/Disable user (Admin only)")
    public ResponseEntity<ApiResponse<UserDto>> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean enabled) {
        ApiResponse<UserDto> response = adminService.updateUserStatus(userId, enabled);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete user (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        ApiResponse<Void> response = adminService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<ApiResponse<Object>> getDashboardStats() {
        ApiResponse<Object> response = adminService.getDashboardStats();
        return ResponseEntity.ok(response);
    }
}
