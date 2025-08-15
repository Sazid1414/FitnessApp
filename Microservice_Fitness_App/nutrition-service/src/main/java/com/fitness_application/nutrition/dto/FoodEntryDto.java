package com.fitness_application.nutrition.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodEntryDto {
    private Long id;
    private String foodName;
    private String brand; // Match entity field name
    private String barcode;
    private Double quantity; // Match entity field name
    private String unit; // Match entity field name
    private Integer calories; // Match entity field type
    private Double protein;
    private Double carbs; // Match entity field name
    private Double fat;
    private Double fiber;
    private Double sugar;
    private Double sodium;
    private Double cholesterol;
    private Double vitaminC;
    private Double calcium;
    private Double iron;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long nutritionLogId; // For API convenience
}
