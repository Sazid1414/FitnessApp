package com.fitness_application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workouts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workout {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private WorkoutType type;
    
    private Integer durationMinutes;
    private Integer caloriesBurned;
    private String notes;
    
    @Enumerated(EnumType.STRING)
    private IntensityLevel intensity;
    
    @Column(name = "workout_date")
    private LocalDateTime workoutDate;
    
    @Column(name = "completed")
    private boolean completed = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exercise> exercises;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum WorkoutType {
        CARDIO, STRENGTH, FLEXIBILITY, SPORTS, YOGA, PILATES, HIIT, CROSSFIT, RUNNING, CYCLING, SWIMMING
    }
    
    public enum IntensityLevel {
        LOW, MODERATE, HIGH, VERY_HIGH
    }
}
