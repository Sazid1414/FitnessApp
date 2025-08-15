package com.fitness_application.workout.dto;

import java.time.LocalDateTime;

import com.fitness_application.workout.model.Exercise.ExerciseCategory;
import com.fitness_application.workout.model.Exercise.MuscleGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private Long id;
    private String name;
    private String description;
    private ExerciseCategory category;
    private MuscleGroup primaryMuscleGroup;
    private MuscleGroup secondaryMuscleGroup;
    private String instructions;
    private String equipment;
    private String tips;
    
    // Performance data
    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer durationSeconds;
    private Double distance;
    private Integer restSeconds;
    private String notes;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
