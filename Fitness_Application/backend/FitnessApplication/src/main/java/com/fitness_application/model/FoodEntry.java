package com.fitness_application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "food_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String foodName;
    
    private String brand;
    private Double quantity; // serving size
    private String unit; // e.g., "grams", "cups", "pieces"
    
    private Integer calories;
    private Double protein; // in grams
    private Double carbs; // in grams
    private Double fat; // in grams
    private Double fiber; // in grams
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrition_log_id", nullable = false)
    private NutritionLog nutritionLog;
}
