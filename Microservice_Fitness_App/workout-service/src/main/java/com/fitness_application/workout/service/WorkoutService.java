package com.fitness_application.workout.service;

import com.fitness_application.workout.dto.ApiResponse;
import com.fitness_application.workout.dto.WorkoutDto;
import com.fitness_application.workout.dto.ExerciseDto;
import com.fitness_application.workout.model.Workout;
import com.fitness_application.workout.model.Exercise;
import com.fitness_application.workout.repository.WorkoutRepository;
import com.fitness_application.workout.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkoutService {
    
    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final ModelMapper modelMapper;
    
    public ApiResponse<WorkoutDto> createWorkout(Long userId, WorkoutDto workoutDto) {
        try {
            log.info("Creating workout for user: {}", userId);
            
            Workout workout = new Workout();
            workout.setUserId(userId);
            workout.setName(workoutDto.getName());
            workout.setType(workoutDto.getType());
            workout.setDescription(workoutDto.getDescription());
            workout.setDurationMinutes(workoutDto.getDurationMinutes());
            workout.setCaloriesBurned(workoutDto.getCaloriesBurned());
            workout.setWorkoutDate(workoutDto.getWorkoutDate() != null ? workoutDto.getWorkoutDate() : LocalDateTime.now());
            workout.setCompleted(false);
            workout.setNotes(workoutDto.getNotes());
            
            Workout savedWorkout = workoutRepository.save(workout);
            
            // Create exercises if provided
            if (workoutDto.getExercises() != null && !workoutDto.getExercises().isEmpty()) {
                List<Exercise> exercises = workoutDto.getExercises().stream()
                        .map(exerciseDto -> {
                            Exercise exercise = new Exercise();
                            exercise.setWorkout(savedWorkout);
                            exercise.setName(exerciseDto.getName());
                            exercise.setCategory(exerciseDto.getCategory());
                            exercise.setSets(exerciseDto.getSets());
                            exercise.setReps(exerciseDto.getReps());
                            exercise.setWeight(exerciseDto.getWeight());
                            exercise.setDurationSeconds(exerciseDto.getDurationSeconds());
                            exercise.setNotes(exerciseDto.getNotes());
                            return exercise;
                        })
                        .collect(Collectors.toList());
                
                exerciseRepository.saveAll(exercises);
                savedWorkout.setExercises(exercises);
            }
            
            WorkoutDto responseDto = modelMapper.map(savedWorkout, WorkoutDto.class);
            return ApiResponse.success("Workout created successfully", responseDto);
            
        } catch (Exception e) {
            log.error("Error creating workout for user {}: {}", userId, e.getMessage(), e);
            return ApiResponse.error("Failed to create workout: " + e.getMessage());
        }
    }
    
    public ApiResponse<List<WorkoutDto>> getUserWorkouts(Long userId, int page, int size) {
        try {
            log.info("Fetching workouts for user: {}, page: {}, size: {}", userId, page, size);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Workout> workoutPage = workoutRepository.findByUserIdOrderByWorkoutDateDesc(userId, pageable);
            
            List<WorkoutDto> workoutDtos = workoutPage.getContent().stream()
                    .map(workout -> {
                        WorkoutDto dto = modelMapper.map(workout, WorkoutDto.class);
                        // Load exercises
                        List<Exercise> exercises = exerciseRepository.findByWorkoutIdOrderById(workout.getId());
                        List<ExerciseDto> exerciseDtos = exercises.stream()
                                .map(exercise -> modelMapper.map(exercise, ExerciseDto.class))
                                .collect(Collectors.toList());
                        dto.setExercises(exerciseDtos);
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return ApiResponse.success("Workouts retrieved successfully", workoutDtos);
            
        } catch (Exception e) {
            log.error("Error fetching workouts for user {}: {}", userId, e.getMessage(), e);
            return ApiResponse.error("Failed to fetch workouts: " + e.getMessage());
        }
    }
    
    public ApiResponse<WorkoutDto> getWorkoutById(Long userId, Long workoutId) {
        try {
            log.info("Fetching workout {} for user: {}", workoutId, userId);
            
            Workout workout = workoutRepository.findByIdAndUserId(workoutId, userId)
                    .orElseThrow(() -> new RuntimeException("Workout not found"));
            
            WorkoutDto workoutDto = modelMapper.map(workout, WorkoutDto.class);
            
            // Load exercises
            List<Exercise> exercises = exerciseRepository.findByWorkoutIdOrderById(workoutId);
            List<ExerciseDto> exerciseDtos = exercises.stream()
                    .map(exercise -> modelMapper.map(exercise, ExerciseDto.class))
                    .collect(Collectors.toList());
            workoutDto.setExercises(exerciseDtos);
            
            return ApiResponse.success("Workout retrieved successfully", workoutDto);
            
        } catch (Exception e) {
            log.error("Error fetching workout {} for user {}: {}", workoutId, userId, e.getMessage(), e);
            return ApiResponse.error("Failed to fetch workout: " + e.getMessage());
        }
    }
    
    public ApiResponse<WorkoutDto> updateWorkout(Long userId, Long workoutId, WorkoutDto workoutDto) {
        try {
            log.info("Updating workout {} for user: {}", workoutId, userId);
            
            Workout workout = workoutRepository.findByIdAndUserId(workoutId, userId)
                    .orElseThrow(() -> new RuntimeException("Workout not found"));
            
            // Update workout fields
            workout.setName(workoutDto.getName());
            workout.setType(workoutDto.getType());
            workout.setDescription(workoutDto.getDescription());
            workout.setDurationMinutes(workoutDto.getDurationMinutes());
            workout.setCaloriesBurned(workoutDto.getCaloriesBurned());
            workout.setNotes(workoutDto.getNotes());
            
            // Update exercises
            if (workoutDto.getExercises() != null) {
                // Delete existing exercises
                exerciseRepository.deleteByWorkoutId(workoutId);
                
                // Create new exercises
                List<Exercise> exercises = workoutDto.getExercises().stream()
                        .map(exerciseDto -> {
                            Exercise exercise = new Exercise();
                            exercise.setWorkout(workout);
                            exercise.setName(exerciseDto.getName());
                            exercise.setCategory(exerciseDto.getCategory());
                            exercise.setSets(exerciseDto.getSets());
                            exercise.setReps(exerciseDto.getReps());
                            exercise.setWeight(exerciseDto.getWeight());
                            exercise.setDurationSeconds(exerciseDto.getDurationSeconds());
                            exercise.setNotes(exerciseDto.getNotes());
                            return exercise;
                        })
                        .collect(Collectors.toList());
                
                exerciseRepository.saveAll(exercises);
                workout.setExercises(exercises);
            }
            
            Workout savedWorkout = workoutRepository.save(workout);
            WorkoutDto responseDto = modelMapper.map(savedWorkout, WorkoutDto.class);
            
            return ApiResponse.success("Workout updated successfully", responseDto);
            
        } catch (Exception e) {
            log.error("Error updating workout {} for user {}: {}", workoutId, userId, e.getMessage(), e);
            return ApiResponse.error("Failed to update workout: " + e.getMessage());
        }
    }
    
    public ApiResponse<Void> deleteWorkout(Long userId, Long workoutId) {
        try {
            log.info("Deleting workout {} for user: {}", workoutId, userId);
            
            Workout workout = workoutRepository.findByIdAndUserId(workoutId, userId)
                    .orElseThrow(() -> new RuntimeException("Workout not found"));
            
            // Delete exercises first
            exerciseRepository.deleteByWorkoutId(workoutId);
            
            // Delete workout
            workoutRepository.delete(workout);
            
            return ApiResponse.success("Workout deleted successfully", null);
            
        } catch (Exception e) {
            log.error("Error deleting workout {} for user {}: {}", workoutId, userId, e.getMessage(), e);
            return ApiResponse.error("Failed to delete workout: " + e.getMessage());
        }
    }
    
    public ApiResponse<WorkoutDto> completeWorkout(Long userId, Long workoutId) {
        try {
            log.info("Completing workout {} for user: {}", workoutId, userId);
            
            Workout workout = workoutRepository.findByIdAndUserId(workoutId, userId)
                    .orElseThrow(() -> new RuntimeException("Workout not found"));
            
            workout.setCompleted(true);
            
            Workout savedWorkout = workoutRepository.save(workout);
            WorkoutDto workoutDto = modelMapper.map(savedWorkout, WorkoutDto.class);
            
            return ApiResponse.success("Workout completed successfully", workoutDto);
            
        } catch (Exception e) {
            log.error("Error completing workout {} for user {}: {}", workoutId, userId, e.getMessage(), e);
            return ApiResponse.error("Failed to complete workout: " + e.getMessage());
        }
    }
    
    public ApiResponse<List<WorkoutDto>> getCompletedWorkouts(Long userId) {
        try {
            log.info("Fetching completed workouts for user: {}", userId);
            
            List<Workout> workouts = workoutRepository.findByUserIdAndCompleted(userId, true);
            List<WorkoutDto> workoutDtos = workouts.stream()
                    .map(workout -> modelMapper.map(workout, WorkoutDto.class))
                    .collect(Collectors.toList());
            
            return ApiResponse.success("Completed workouts retrieved successfully", workoutDtos);
            
        } catch (Exception e) {
            log.error("Error fetching completed workouts for user {}: {}", userId, e.getMessage(), e);
            return ApiResponse.error("Failed to fetch completed workouts: " + e.getMessage());
        }
    }
    
    public ApiResponse<Long> getWorkoutCount(Long userId) {
        try {
            long count = workoutRepository.countWorkoutsByUser(userId);
            return ApiResponse.success("Workout count retrieved successfully", count);
        } catch (Exception e) {
            log.error("Error getting workout count for user {}: {}", userId, e.getMessage(), e);
            return ApiResponse.error("Failed to get workout count: " + e.getMessage());
        }
    }
}
