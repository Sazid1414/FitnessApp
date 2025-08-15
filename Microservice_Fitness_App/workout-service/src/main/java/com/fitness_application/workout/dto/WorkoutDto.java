package com.fitness_application.workout.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fitness_application.workout.model.Workout.IntensityLevel;
import com.fitness_application.workout.model.Workout.WorkoutType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDto {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private WorkoutType type;
    private Integer durationMinutes;
    private Integer caloriesBurned;
    private String notes;
    private IntensityLevel intensity;
    private LocalDateTime workoutDate;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ExerciseDto> exercises;
}
