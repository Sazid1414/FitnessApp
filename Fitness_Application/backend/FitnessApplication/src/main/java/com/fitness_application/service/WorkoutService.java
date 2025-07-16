package com.fitness_application.service;

import com.fitness_application.dto.ApiResponse;
import com.fitness_application.dto.WorkoutDto;
import com.fitness_application.exception.ResourceNotFoundException;
import com.fitness_application.model.User;
import com.fitness_application.model.Workout;
import com.fitness_application.repository.UserRepository;
import com.fitness_application.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
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
public class WorkoutService {
    
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    
    public ApiResponse<WorkoutDto> createWorkout(String userEmail, WorkoutDto workoutDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Workout workout = modelMapper.map(workoutDto, Workout.class);
        workout.setUser(user);
        workout.setCreatedAt(LocalDateTime.now());
        workout.setUpdatedAt(LocalDateTime.now());
        
        Workout savedWorkout = workoutRepository.save(workout);
        WorkoutDto responseDto = modelMapper.map(savedWorkout, WorkoutDto.class);
        responseDto.setUserId(user.getId());
        
        return ApiResponse.success("Workout created successfully", responseDto);
    }
    
    public ApiResponse<List<WorkoutDto>> getUserWorkouts(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Workout> workoutPage = workoutRepository.findByUserOrderByWorkoutDateDesc(user, pageable);
        
        List<WorkoutDto> workoutDtos = workoutPage.getContent().stream()
                .map(workout -> {
                    WorkoutDto dto = modelMapper.map(workout, WorkoutDto.class);
                    dto.setUserId(user.getId());
                    return dto;
                })
                .collect(Collectors.toList());
        
        return ApiResponse.success("Workouts retrieved successfully", workoutDtos);
    }
    
    public ApiResponse<WorkoutDto> getWorkoutById(String userEmail, Long workoutId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found"));
        
        if (!workout.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Workout not found");
        }
        
        WorkoutDto workoutDto = modelMapper.map(workout, WorkoutDto.class);
        workoutDto.setUserId(user.getId());
        
        return ApiResponse.success("Workout retrieved successfully", workoutDto);
    }
    
    public ApiResponse<WorkoutDto> updateWorkout(String userEmail, Long workoutId, WorkoutDto workoutDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found"));
        
        if (!workout.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Workout not found");
        }
        
        // Update workout fields
        workout.setName(workoutDto.getName());
        workout.setDescription(workoutDto.getDescription());
        workout.setType(workoutDto.getType());
        workout.setDurationMinutes(workoutDto.getDurationMinutes());
        workout.setCaloriesBurned(workoutDto.getCaloriesBurned());
        workout.setNotes(workoutDto.getNotes());
        workout.setIntensity(workoutDto.getIntensity());
        workout.setWorkoutDate(workoutDto.getWorkoutDate());
        workout.setCompleted(workoutDto.isCompleted());
        workout.setUpdatedAt(LocalDateTime.now());
        
        Workout updatedWorkout = workoutRepository.save(workout);
        WorkoutDto responseDto = modelMapper.map(updatedWorkout, WorkoutDto.class);
        responseDto.setUserId(user.getId());
        
        return ApiResponse.success("Workout updated successfully", responseDto);
    }
    
    public ApiResponse<Void> deleteWorkout(String userEmail, Long workoutId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found"));
        
        if (!workout.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Workout not found");
        }
        
        workoutRepository.delete(workout);
        return ApiResponse.success("Workout deleted successfully");
    }
    
    public ApiResponse<WorkoutDto> completeWorkout(String userEmail, Long workoutId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found"));
        
        if (!workout.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Workout not found");
        }
        
        workout.setCompleted(true);
        workout.setUpdatedAt(LocalDateTime.now());
        
        Workout updatedWorkout = workoutRepository.save(workout);
        WorkoutDto responseDto = modelMapper.map(updatedWorkout, WorkoutDto.class);
        responseDto.setUserId(user.getId());
        
        return ApiResponse.success("Workout completed successfully", responseDto);
    }
}
