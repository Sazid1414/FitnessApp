package com.fitness_application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private GoalType type;
    
    private Double targetValue;
    private Double currentValue = 0.0;
    private String unit; // e.g., "kg", "minutes", "times"
    
    @Column(name = "target_date")
    private LocalDate targetDate;
    
    @Enumerated(EnumType.STRING)
    private GoalStatus status = GoalStatus.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum GoalType {
        WEIGHT_LOSS, WEIGHT_GAIN, MUSCLE_GAIN, ENDURANCE, STRENGTH, FLEXIBILITY, CONSISTENCY
    }
    
    public enum GoalStatus {
        ACTIVE, COMPLETED, PAUSED, CANCELLED
    }
}
