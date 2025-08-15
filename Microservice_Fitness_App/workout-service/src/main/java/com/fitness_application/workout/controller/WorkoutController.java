package com.fitness_application.workout.controller;

import com.fitness_application.workout.dto.ApiResponse;
import com.fitness_application.workout.dto.WorkoutDto;
import com.fitness_application.workout.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
@Tag(name = "Workouts", description = "Workout management APIs")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@Slf4j
public class WorkoutController {
    
    private final WorkoutService workoutService;
    
    @PostMapping
    @Operation(summary = "Create new workout")
    public ResponseEntity<ApiResponse<WorkoutDto>> createWorkout(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody WorkoutDto workoutDto) {
        log.info("Creating workout for user: {}", userId);
        ApiResponse<WorkoutDto> response = workoutService.createWorkout(userId, workoutDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @Operation(summary = "Get user workouts")
    public ResponseEntity<ApiResponse<List<WorkoutDto>>> getUserWorkouts(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching workouts for user: {}", userId);
        ApiResponse<List<WorkoutDto>> response = workoutService.getUserWorkouts(userId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{workoutId}")
    @Operation(summary = "Get workout by ID")
    public ResponseEntity<ApiResponse<WorkoutDto>> getWorkoutById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long workoutId) {
        log.info("Fetching workout {} for user: {}", workoutId, userId);
        ApiResponse<WorkoutDto> response = workoutService.getWorkoutById(userId, workoutId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{workoutId}")
    @Operation(summary = "Update workout")
    public ResponseEntity<ApiResponse<WorkoutDto>> updateWorkout(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long workoutId,
            @Valid @RequestBody WorkoutDto workoutDto) {
        log.info("Updating workout {} for user: {}", workoutId, userId);
        ApiResponse<WorkoutDto> response = workoutService.updateWorkout(userId, workoutId, workoutDto);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{workoutId}")
    @Operation(summary = "Delete workout")
    public ResponseEntity<ApiResponse<Void>> deleteWorkout(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long workoutId) {
        log.info("Deleting workout {} for user: {}", workoutId, userId);
        ApiResponse<Void> response = workoutService.deleteWorkout(userId, workoutId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{workoutId}/complete")
    @Operation(summary = "Mark workout as completed")
    public ResponseEntity<ApiResponse<WorkoutDto>> completeWorkout(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long workoutId) {
        log.info("Completing workout {} for user: {}", workoutId, userId);
        ApiResponse<WorkoutDto> response = workoutService.completeWorkout(userId, workoutId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/completed")
    @Operation(summary = "Get completed workouts")
    public ResponseEntity<ApiResponse<List<WorkoutDto>>> getCompletedWorkouts(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Fetching completed workouts for user: {}", userId);
        ApiResponse<List<WorkoutDto>> response = workoutService.getCompletedWorkouts(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/count")
    @Operation(summary = "Get workout count")
    public ResponseEntity<ApiResponse<Long>> getWorkoutCount(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Fetching workout count for user: {}", userId);
        ApiResponse<Long> response = workoutService.getWorkoutCount(userId);
        return ResponseEntity.ok(response);
    }
}
