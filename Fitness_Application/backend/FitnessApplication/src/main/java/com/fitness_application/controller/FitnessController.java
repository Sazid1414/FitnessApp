package com.fitness_application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitness_application.domain.FitnessRecommendation;
import com.fitness_application.domain.service.FitnessCalculationService;
import com.fitness_application.model.User;
import com.fitness_application.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for fitness calculations and recommendations
 * Demonstrates clean architecture with domain services
 */
@RestController
@RequestMapping("/api/v1/fitness")
@Tag(name = "Fitness", description = "Fitness calculations and recommendations")
@org.springframework.security.access.prepost.PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class FitnessController {

    @Autowired
    private FitnessCalculationService fitnessCalculationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get comprehensive fitness recommendations for the authenticated user
     */
    @GetMapping("/recommendations")
    @Operation(summary = "Get personalized fitness recommendations", 
               description = "Returns comprehensive fitness recommendations including BMI, calorie needs, macro split, and workout suggestions")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FitnessRecommendation> getRecommendations(Authentication authentication) {
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.hasCompleteProfile()) {
            return ResponseEntity.badRequest()
                .build();
        }
        
        FitnessRecommendation recommendation = fitnessCalculationService.generateRecommendation(user);
        return ResponseEntity.ok(recommendation);
    }

    /**
     * Calculate BMI for given height and weight
     */
    @GetMapping("/bmi")
    @Operation(summary = "Calculate BMI", 
               description = "Calculate Body Mass Index from height (cm) and weight (kg)")
    public ResponseEntity<BmiResult> calculateBMI(
            @RequestParam Double height, 
            @RequestParam Double weight) {
        
        try {
            double bmi = fitnessCalculationService.calculateBMI(height, weight);
            String category = fitnessCalculationService.getBMICategory(bmi);
            
            return ResponseEntity.ok(new BmiResult(bmi, category));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Calculate daily water intake recommendation
     */
    @GetMapping("/water-intake")
    @Operation(summary = "Calculate water intake", 
               description = "Calculate daily water intake recommendation based on weight")
    public ResponseEntity<WaterIntakeResult> calculateWaterIntake(@RequestParam Double weight) {
        try {
            double waterIntake = fitnessCalculationService.calculateWaterIntake(weight);
            return ResponseEntity.ok(new WaterIntakeResult(waterIntake));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get user's current fitness metrics
     */
    @GetMapping("/metrics")
    @Operation(summary = "Get current fitness metrics", 
               description = "Get current user's basic fitness metrics (BMR, daily calories, BMI)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FitnessMetrics> getFitnessMetrics(Authentication authentication) {
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.hasCompleteProfile()) {
            return ResponseEntity.badRequest().build();
        }
        
        double bmr = user.calculateBasalMetabolicRate();
        double dailyCalories = user.calculateDailyCalorieNeeds();
        double targetCalories = user.calculateTargetCalories();
        double bmi = fitnessCalculationService.calculateBMI(user.getHeight(), user.getWeight());
        
        FitnessMetrics metrics = new FitnessMetrics(bmr, dailyCalories, targetCalories, bmi);
        return ResponseEntity.ok(metrics);
    }

    // DTOs for API responses
    public record BmiResult(double bmi, String category) {}
    public record WaterIntakeResult(double dailyIntakeML) {}
    public record FitnessMetrics(double bmr, double dailyCalories, double targetCalories, double bmi) {}
}
