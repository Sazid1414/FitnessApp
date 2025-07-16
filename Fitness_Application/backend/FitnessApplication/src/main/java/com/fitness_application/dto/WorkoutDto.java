package com.fitness_application.dto;

import com.fitness_application.model.Workout;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDto {
    private Long id;
    
    @NotBlank(message = "Workout name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Workout type is required")
    private Workout.WorkoutType type;
    
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;
    
    private Integer caloriesBurned;
    private String notes;
    private Workout.IntensityLevel intensity;
    
    @NotNull(message = "Workout date is required")
    private LocalDateTime workoutDate;
    
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private List<ExerciseDto> exercises;
}
