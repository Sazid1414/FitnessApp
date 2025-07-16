package com.fitness_application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private Long id;
    
    @NotBlank(message = "Exercise name is required")
    private String name;
    
    private String description;
    
    @Positive(message = "Sets must be positive")
    private Integer sets;
    
    @Positive(message = "Reps must be positive")
    private Integer reps;
    
    private Double weight;
    private Integer durationSeconds;
    private Double distance;
    private Integer caloriesBurned;
    private String notes;
    private LocalDateTime createdAt;
    private Long workoutId;
}
