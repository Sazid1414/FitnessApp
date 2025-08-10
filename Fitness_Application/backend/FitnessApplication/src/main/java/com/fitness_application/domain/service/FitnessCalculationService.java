package com.fitness_application.domain.service;

import com.fitness_application.domain.FitnessRecommendation;
import com.fitness_application.model.User;
import org.springframework.stereotype.Service;

/**
 * Fitness Calculation Service
 * 
 * Clean implementation following SOLID principles:
 * - Single Responsibility: Only handles fitness calculations
 * - Stateless service that operates on domain objects
 * - Simple, focused methods
 */
@Service
public class FitnessCalculationService {
    
    /**
     * Calculate Body Mass Index (BMI)
     * Simple domain calculation
     */
    public double calculateBMI(double height, double weight) {
        if (height <= 0 || weight <= 0) {
            throw new IllegalArgumentException("Height and weight must be positive values");
        }
        
        double heightInMeters = height / 100; // Convert cm to meters
        return weight / (heightInMeters * heightInMeters);
    }
    
    /**
     * Get BMI category based on WHO standards
     * Simple business rule
     */
    public String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25) return "Normal";
        if (bmi < 30) return "Overweight";
        return "Obese";
    }
    
    /**
     * Calculate recommended water intake
     * Simple calculation: 35ml per kg of body weight
     */
    public double calculateWaterIntake(double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        return weight * 35; // ml per day
    }
    
    /**
     * Generate comprehensive fitness recommendations
     * Uses existing domain logic from User entity and enums
     */
    public FitnessRecommendation generateRecommendation(User user) {
        if (!user.hasCompleteProfile()) {
            throw new IllegalStateException("User profile must be complete to generate recommendations");
        }
        
        double bmr = user.calculateBasalMetabolicRate();
        double dailyCalories = user.calculateDailyCalorieNeeds();
        double targetCalories = user.calculateTargetCalories();
        double bmi = calculateBMI(user.getHeight(), user.getWeight());
        String bmiCategory = getBMICategory(bmi);
        double waterIntake = calculateWaterIntake(user.getWeight());
        
        // Get recommendations from domain enums
        var macros = user.getFitnessGoal().calculateMacroSplit(targetCalories);
        var workoutRecommendations = user.getActivityLevel().getWorkoutRecommendations();
        
        return new FitnessRecommendation(
            bmr,
            dailyCalories,
            bmi,
            bmiCategory,
            waterIntake,
            macros,
            workoutRecommendations
        );
    }
}
