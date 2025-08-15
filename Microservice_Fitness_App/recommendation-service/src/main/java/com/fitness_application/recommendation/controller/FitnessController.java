package com.fitness_application.recommendation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitness_application.recommendation.dto.ApiResponse;
import com.fitness_application.recommendation.dto.FitnessRecommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fitness")
@RequiredArgsConstructor
@Tag(name = "Fitness", description = "Fitness calculations and recommendations")
public class FitnessController {
    
    @GetMapping("/recommendations")
    @Operation(summary = "Get personalized fitness recommendations")
    public ResponseEntity<ApiResponse<FitnessRecommendation>> getRecommendations(
            @RequestHeader("Authorization") String authToken) {
        
        // Mock response for now
        FitnessRecommendation recommendation = new FitnessRecommendation();
        recommendation.setBmi(22.5);
        recommendation.setBmr(1800.0);
        recommendation.setDailyCalorieNeeds(2200.0);
        recommendation.setTargetCalories(2200.0);
        recommendation.setWaterIntakeML(2500.0);
        recommendation.setRecommendedWorkoutDuration(45);
        recommendation.setWorkoutFrequencyPerWeek(4);
        
        ApiResponse<FitnessRecommendation> response = ApiResponse.success(recommendation);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/bmi")
    @Operation(summary = "Calculate BMI")
    public ResponseEntity<ApiResponse<BmiResult>> calculateBMI(
            @RequestParam Double height, 
            @RequestParam Double weight) {
        
        try {
            double bmi = weight / ((height / 100) * (height / 100));
            String category = getBMICategory(bmi);
            
            BmiResult result = new BmiResult(bmi, category);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid input parameters"));
        }
    }
    
    @GetMapping("/water-intake")
    @Operation(summary = "Calculate water intake")
    public ResponseEntity<ApiResponse<WaterIntakeResult>> calculateWaterIntake(@RequestParam Double weight) {
        try {
            double waterIntake = weight * 35; // 35ml per kg body weight
            WaterIntakeResult result = new WaterIntakeResult(waterIntake);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid weight parameter"));
        }
    }
    
    @GetMapping("/metrics")
    @Operation(summary = "Get current fitness metrics")
    public ResponseEntity<ApiResponse<FitnessMetrics>> getFitnessMetrics(
            @RequestHeader("Authorization") String authToken) {
        
        // Mock response - in real implementation, fetch user data and calculate
        FitnessMetrics metrics = new FitnessMetrics(1800.0, 2200.0, 2200.0, 22.5);
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }
    
    @GetMapping("/health")
    @Operation(summary = "Service health check")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Recommendation Service is healthy");
    }
    
    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25) return "Normal weight";
        else if (bmi < 30) return "Overweight";
        else return "Obese";
    }
    
    // DTOs for API responses
    public record BmiResult(double bmi, String category) {}
    public record WaterIntakeResult(double dailyIntakeML) {}
    public record FitnessMetrics(double bmr, double dailyCalories, double targetCalories, double bmi) {}
}
