package com.fitness_application.domain.enums;

/**
 * Activity Level enumeration following clean architecture principles.
 * 
 * Represents different levels of physical activity in the fitness domain.
 * Contains domain logic specific to activity levels and calorie calculations.
 * 
 * Following SOLID principles:
 * - SRP: Only handles activity level concerns
 * - OCP: Can be extended with new activity levels
 * - LSP: All activity levels can be used interchangeably
 * - ISP: Clients depend only on methods they use
 * - DIP: Higher-level modules depend on this abstraction
 */
public enum ActivityLevel {
    SEDENTARY("Sedentary", 1.2, "Little or no exercise"),
    LIGHTLY_ACTIVE("Lightly Active", 1.375, "Light exercise 1-3 days/week"),
    MODERATELY_ACTIVE("Moderately Active", 1.55, "Moderate exercise 3-5 days/week"),
    VERY_ACTIVE("Very Active", 1.725, "Hard exercise 6-7 days/week"),
    EXTREMELY_ACTIVE("Extremely Active", 1.9, "Very hard exercise, physical job");
    
    private final String displayName;
    private final double multiplier; // BMR multiplier for calorie calculations
    private final String description;
    
    ActivityLevel(String displayName, double multiplier, String description) {
        this.displayName = displayName;
        this.multiplier = multiplier;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getMultiplier() {
        return multiplier;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Domain logic: Calculate daily calorie needs based on BMR
     * This is business logic that belongs in the domain layer
     */
    public double calculateDailyCalories(double basalMetabolicRate) {
        return basalMetabolicRate * multiplier;
    }
    
    /**
     * Get workout recommendations based on activity level
     * Domain business logic for fitness guidance
     */
    public java.util.List<String> getWorkoutRecommendations() {
        return switch (this) {
            case SEDENTARY -> java.util.List.of(
                "Start with 10-15 minutes of light walking daily",
                "Add basic bodyweight exercises 2-3 times per week",
                "Focus on building consistency before intensity"
            );
            case LIGHTLY_ACTIVE -> java.util.List.of(
                "Increase to 30 minutes of moderate exercise most days",
                "Add strength training 2 times per week",
                "Try activities like swimming, cycling, or dancing"
            );
            case MODERATELY_ACTIVE -> java.util.List.of(
                "Maintain current activity levels",
                "Add variety with different types of workouts",
                "Consider increasing intensity gradually"
            );
            case VERY_ACTIVE -> java.util.List.of(
                "Focus on specific fitness goals",
                "Incorporate both cardio and strength training",
                "Monitor for overtraining and ensure adequate recovery"
            );
            case EXTREMELY_ACTIVE -> java.util.List.of(
                "Periodize training to prevent burnout",
                "Focus on recovery and nutrition optimization",
                "Consider working with a trainer for advanced programming"
            );
        };
    }
    
    /**
     * Get recommended workout frequency per week
     */
    public int getRecommendedWorkoutFrequency() {
        return switch (this) {
            case SEDENTARY -> 2;
            case LIGHTLY_ACTIVE -> 3;
            case MODERATELY_ACTIVE -> 4;
            case VERY_ACTIVE -> 5;
            case EXTREMELY_ACTIVE -> 6;
        };
    }
}
