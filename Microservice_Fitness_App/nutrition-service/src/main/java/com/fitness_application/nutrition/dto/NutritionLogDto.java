package com.fitness_application.nutrition.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionLogDto {
    private Long id;
    private Long userId;
    private LocalDate logDate;
    private String mealType; // Match entity enum as string
    private Integer totalCalories; // Match entity field type
    private Double totalProtein;
    private Double totalCarbs; // Match entity field name
    private Double totalFat;
    private Double totalFiber;
    private Double totalSugar;
    private Double totalSodium;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FoodEntryDto> foodEntries;
    
    // Convenience method for date as string (for backward compatibility)
    public String getDate() {
        return logDate != null ? logDate.toString() : null;
    }
    
    public void setDate(String dateString) {
        if (dateString != null) {
            this.logDate = LocalDate.parse(dateString);
        }
    }
}
