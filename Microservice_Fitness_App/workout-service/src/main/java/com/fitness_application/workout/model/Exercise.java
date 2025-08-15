package com.fitness_application.workout.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private ExerciseCategory category;
    
    @Enumerated(EnumType.STRING)
    private MuscleGroup primaryMuscleGroup;
    
    @Enumerated(EnumType.STRING)
    private MuscleGroup secondaryMuscleGroup;
    
    private String instructions;
    private String equipment;
    private String tips;
    
    // Exercise performance data
    private Integer sets;
    private Integer reps;
    private Double weight; // in kg
    private Integer durationSeconds;
    private Double distance; // in meters
    private Integer restSeconds;
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ExerciseCategory {
        STRENGTH, CARDIO, FLEXIBILITY, BALANCE, PLYOMETRIC, CORE, FUNCTIONAL
    }
    
    public enum MuscleGroup {
        CHEST, BACK, SHOULDERS, ARMS, BICEPS, TRICEPS, FOREARMS, 
        ABS, CORE, LEGS, QUADRICEPS, HAMSTRINGS, CALVES, GLUTES, 
        FULL_BODY, CARDIO
    }
}
