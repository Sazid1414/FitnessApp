package com.fitness_application.nutrition.model;

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
    
    @Column(nullable = false)
    private String brand; // Food brand or "Generic"
    
    private Double quantity = 1.0;
    private String unit = "serving"; // serving, cup, gram, ounce, etc.
    
    // Nutritional information per serving
    private Integer calories;
    private Double protein = 0.0; // in grams
    private Double carbs = 0.0; // in grams
    private Double fat = 0.0; // in grams
    private Double fiber = 0.0; // in grams
    private Double sugar = 0.0; // in grams
    private Double sodium = 0.0; // in mg
    private Double cholesterol = 0.0; // in mg
    private Double vitaminC = 0.0; // in mg
    private Double calcium = 0.0; // in mg
    private Double iron = 0.0; // in mg
    
    private String barcode; // For food database lookup
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrition_log_id", nullable = false)
    private NutritionLog nutritionLog;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Calculate total nutrition values based on quantity
    public Integer getTotalCalories() {
        return calories != null && quantity != null ? (int) (calories * quantity) : 0;
    }
    
    public Double getTotalProtein() {
        return protein != null && quantity != null ? protein * quantity : 0.0;
    }
    
    public Double getTotalCarbs() {
        return carbs != null && quantity != null ? carbs * quantity : 0.0;
    }
    
    public Double getTotalFat() {
        return fat != null && quantity != null ? fat * quantity : 0.0;
    }
    
    public Double getTotalFiber() {
        return fiber != null && quantity != null ? fiber * quantity : 0.0;
    }
    
    public Double getTotalSugar() {
        return sugar != null && quantity != null ? sugar * quantity : 0.0;
    }
    
    public Double getTotalSodium() {
        return sodium != null && quantity != null ? sodium * quantity : 0.0;
    }
}
