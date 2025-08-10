package com.fitness_application.domain.enums;

import java.util.List;

/**
 * Fitness Goal enumeration following Domain-Driven Design.
 * 
 * Represents different fitness objectives in the domain.
 * Contains business logic related to goal achievement and recommendations.
 * 
 * Clean Architecture Benefits:
 * - Domain-centric: Pure business concepts
 * - Independent: No dependencies on frameworks or infrastructure
 * - Testable: Easy to unit test business logic
 * - Extensible: New goals can be added without breaking existing code
 */
public enum FitnessGoal {
    LOSE_WEIGHT("Lose Weight", "weight_loss", -500, List.of("cardio", "strength")),
    GAIN_WEIGHT("Gain Weight", "weight_gain", 500, List.of("strength", "protein")),
    MAINTAIN_WEIGHT("Maintain Weight", "maintenance", 0, List.of("balanced", "consistency")),
    BUILD_MUSCLE("Build Muscle", "muscle_gain", 300, List.of("strength", "protein", "progressive_overload")),
    IMPROVE_ENDURANCE("Improve Endurance", "endurance", -200, List.of("cardio", "interval_training")),
    GENERAL_FITNESS("General Fitness", "general", -100, List.of("balanced", "variety", "consistency"));
    
    private final String displayName;
    private final String category;
    private final int calorieAdjustment; // Daily calorie adjustment from maintenance
    private final List<String> recommendedFocus;
    
    FitnessGoal(String displayName, String category, int calorieAdjustment, List<String> recommendedFocus) {
        this.displayName = displayName;
        this.category = category;
        this.calorieAdjustment = calorieAdjustment;
        this.recommendedFocus = recommendedFocus;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCategory() {
        return category;
    }
    
    public int getCalorieAdjustment() {
        return calorieAdjustment;
    }
    
    public List<String> getRecommendedFocus() {
        return recommendedFocus;
    }
    
    /**
     * Calculate macro split based on fitness goal
     * Returns map with protein, carbs, and fat calorie distributions
     */
    public java.util.Map<String, Double> calculateMacroSplit(double totalCalories) {
        MacroSplit macroSplit = getRecommendedMacroSplit();
        
        double proteinCalories = totalCalories * macroSplit.protein();
        double carbCalories = totalCalories * macroSplit.carbs();
        double fatCalories = totalCalories * macroSplit.fats();
        
        java.util.Map<String, Double> macros = new java.util.HashMap<>();
        macros.put("protein", proteinCalories / 4); // 4 calories per gram
        macros.put("carbs", carbCalories / 4);     // 4 calories per gram
        macros.put("fat", fatCalories / 9);        // 9 calories per gram
        macros.put("proteinCalories", proteinCalories);
        macros.put("carbCalories", carbCalories);
        macros.put("fatCalories", fatCalories);
        
        return macros;
    }
    
    /**
     * Adjust daily calories based on goal
     * Domain business logic for calorie modification
     */
    public double adjustCalories(double baseCalories) {
        return baseCalories + calorieAdjustment;
    }
    
    /**
     * Business logic: Determine if goal requires calorie surplus
     */
    public boolean requiresCalorieSurplus() {
        return calorieAdjustment > 0;
    }
    
    /**
     * Business logic: Determine if goal requires calorie deficit
     */
    public boolean requiresCalorieDeficit() {
        return calorieAdjustment < 0;
    }
    
    /**
     * Business logic: Get recommended macro split (protein, carbs, fats)
     */
    public MacroSplit getRecommendedMacroSplit() {
        return switch (this) {
            case LOSE_WEIGHT -> new MacroSplit(0.35, 0.35, 0.30); // Higher protein for satiety
            case GAIN_WEIGHT -> new MacroSplit(0.25, 0.50, 0.25); // Higher carbs for energy
            case BUILD_MUSCLE -> new MacroSplit(0.30, 0.40, 0.30); // Balanced with adequate protein
            case IMPROVE_ENDURANCE -> new MacroSplit(0.20, 0.60, 0.20); // Higher carbs for endurance
            default -> new MacroSplit(0.25, 0.45, 0.30); // Balanced approach
        };
    }
    
    /**
     * Value object for macro split recommendations
     */
    public record MacroSplit(double protein, double carbs, double fats) {
        public MacroSplit {
            if (protein + carbs + fats != 1.0) {
                throw new IllegalArgumentException("Macro percentages must sum to 1.0");
            }
        }
    }
}
