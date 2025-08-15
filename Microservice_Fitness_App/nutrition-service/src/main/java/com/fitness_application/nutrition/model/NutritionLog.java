package com.fitness_application.nutrition.model;

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
    
    @Column(name = "user_id", nullable = false)
    private Long userId; // Reference to user from auth service
    
    @Column(name = "log_date")
    private LocalDate logDate;
    
    @Enumerated(EnumType.STRING)
    private MealType mealType;
    
    private Integer totalCalories = 0;
    private Double totalProtein = 0.0; // in grams
    private Double totalCarbs = 0.0; // in grams
    private Double totalFat = 0.0; // in grams
    private Double totalFiber = 0.0; // in grams
    private Double totalSugar = 0.0; // in grams
    private Double totalSodium = 0.0; // in mg
    
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "nutritionLog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FoodEntry> foodEntries;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business logic to calculate totals from food entries
    public void calculateTotals() {
        if (foodEntries != null && !foodEntries.isEmpty()) {
            totalCalories = foodEntries.stream().mapToInt(entry -> entry.getCalories() != null ? entry.getCalories() : 0).sum();
            totalProtein = foodEntries.stream().mapToDouble(entry -> entry.getProtein() != null ? entry.getProtein() : 0.0).sum();
            totalCarbs = foodEntries.stream().mapToDouble(entry -> entry.getCarbs() != null ? entry.getCarbs() : 0.0).sum();
            totalFat = foodEntries.stream().mapToDouble(entry -> entry.getFat() != null ? entry.getFat() : 0.0).sum();
            totalFiber = foodEntries.stream().mapToDouble(entry -> entry.getFiber() != null ? entry.getFiber() : 0.0).sum();
            totalSugar = foodEntries.stream().mapToDouble(entry -> entry.getSugar() != null ? entry.getSugar() : 0.0).sum();
            totalSodium = foodEntries.stream().mapToDouble(entry -> entry.getSodium() != null ? entry.getSodium() : 0.0).sum();
        }
    }
    
    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACK, PRE_WORKOUT, POST_WORKOUT
    }
}
