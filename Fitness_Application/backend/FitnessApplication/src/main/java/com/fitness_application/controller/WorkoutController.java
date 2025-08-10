package com.fitness_application.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitness_application.dto.ApiResponse;
import com.fitness_application.dto.WorkoutDto;
import com.fitness_application.service.WorkoutService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
@Tag(name = "Workouts", description = "Workout management APIs")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class WorkoutController {
    
    private final WorkoutService workoutService;
    
    @PostMapping
    @Operation(summary = "Create a new workout")
    public ResponseEntity<ApiResponse<WorkoutDto>> createWorkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody WorkoutDto workoutDto) {
        ApiResponse<WorkoutDto> response = workoutService.createWorkout(userDetails.getUsername(), workoutDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @Operation(summary = "Get user workouts with pagination")
    public ResponseEntity<ApiResponse<List<WorkoutDto>>> getUserWorkouts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse<List<WorkoutDto>> response = workoutService.getUserWorkouts(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get workout by ID")
    public ResponseEntity<ApiResponse<WorkoutDto>> getWorkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        ApiResponse<WorkoutDto> response = workoutService.getWorkoutById(userDetails.getUsername(), id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update workout")
    public ResponseEntity<ApiResponse<WorkoutDto>> updateWorkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody WorkoutDto workoutDto) {
        ApiResponse<WorkoutDto> response = workoutService.updateWorkout(userDetails.getUsername(), id, workoutDto);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete workout")
    public ResponseEntity<ApiResponse<Void>> deleteWorkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        ApiResponse<Void> response = workoutService.deleteWorkout(userDetails.getUsername(), id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/complete")
    @Operation(summary = "Mark workout as completed")
    public ResponseEntity<ApiResponse<WorkoutDto>> completeWorkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        ApiResponse<WorkoutDto> response = workoutService.completeWorkout(userDetails.getUsername(), id);
        return ResponseEntity.ok(response);
    }
}
