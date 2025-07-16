package com.fitness_application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "nutrition_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "log_date")
    private LocalDate logDate;
    
    @Enumerated(EnumType.STRING)
    private MealType mealType;
    
    private Integer totalCalories = 0;
    private Double totalProtein = 0.0; // in grams
    private Double totalCarbs = 0.0; // in grams
    private Double totalFat = 0.0; // in grams
    private Double totalFiber = 0.0; // in grams
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "nutritionLog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FoodEntry> foodEntries;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACK
    }
}
