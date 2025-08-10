package com.fitness_application.domain;

import java.util.List;
import java.util.Map;

/**
 * Value Object representing comprehensive fitness recommendations
 * 
 * Immutable object that encapsulates all fitness-related calculations and recommendations.
 * Follows Value Object pattern from Domain-Driven Design:
 * - Immutable
 * - No identity (equals based on values)
 * - Can be safely shared
 * - Rich in domain behavior
 */
public record FitnessRecommendation(
    double basalMetabolicRate,
    double dailyCalorieNeeds,
    double bodyMassIndex,
    String bmiCategory,
    double dailyWaterIntake, // in ml
    Map<String, Double> macroSplit,
    List<String> workoutRecommendations
) {
    
    /**
     * Validation constructor
     */
    public FitnessRecommendation {
        if (basalMetabolicRate <= 0) {
            throw new IllegalArgumentException("BMR must be positive");
        }
        if (dailyCalorieNeeds <= 0) {
            throw new IllegalArgumentException("Daily calorie needs must be positive");
        }
        if (bodyMassIndex <= 0) {
            throw new IllegalArgumentException("BMI must be positive");
        }
        if (dailyWaterIntake <= 0) {
            throw new IllegalArgumentException("Water intake must be positive");
        }
        
        // Ensure immutability by creating defensive copies
        macroSplit = Map.copyOf(macroSplit);
        workoutRecommendations = List.copyOf(workoutRecommendations);
    }
    
    /**
     * Business logic: Check if user needs calorie adjustment
     */
    public boolean needsCalorieAdjustment(double currentIntake) {
        double tolerance = dailyCalorieNeeds * 0.05; // 5% tolerance
        return Math.abs(currentIntake - dailyCalorieNeeds) > tolerance;
    }
    
    /**
     * Business logic: Get health status based on BMI
     */
    public String getHealthStatus() {
        return switch (bmiCategory.toLowerCase()) {
            case "underweight" -> "Consider consulting a healthcare provider about healthy weight gain";
            case "normal weight" -> "Maintain current healthy weight range";
            case "overweight" -> "Consider moderate calorie reduction and increased activity";
            case "obese" -> "Consult healthcare provider for comprehensive weight management plan";
            default -> "Unknown BMI category";
        };
    }
    
    /**
     * Business logic: Get priority recommendations
     */
    public List<String> getPriorityRecommendations() {
        return List.of(
            String.format("Aim for %.0f calories daily", dailyCalorieNeeds),
            String.format("Drink at least %.0f ml of water daily", dailyWaterIntake),
            String.format("Your BMI is %.1f (%s)", bodyMassIndex, bmiCategory),
            "Follow the personalized workout plan"
        );
    }
    
    /**
     * Business logic: Calculate protein needs in grams
     */
    public double getProteinGrams() {
        return macroSplit.getOrDefault("protein", 0.0);
    }
    
    /**
     * Business logic: Calculate carbohydrate needs in grams
     */
    public double getCarbGrams() {
        return macroSplit.getOrDefault("carbs", 0.0);
    }
    
    /**
     * Business logic: Calculate fat needs in grams
     */
    public double getFatGrams() {
        return macroSplit.getOrDefault("fat", 0.0);
    }
}
