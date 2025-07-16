package com.fitness_application.controller;

import com.fitness_application.ai.AIRequest;
import com.fitness_application.ai.AIResponse;
import com.fitness_application.ai.AIService;
import com.fitness_application.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Assistant", description = "AI-powered fitness and nutrition recommendations")
public class AIController {
    
    private final AIService aiService;
    
    @PostMapping("/workout-recommendation")
    @Operation(summary = "Get personalized workout recommendation")
    public ResponseEntity<ApiResponse<AIResponse>> getWorkoutRecommendation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AIRequest request) {
        ApiResponse<AIResponse> response = aiService.getWorkoutRecommendation(
                userDetails.getUsername(), request.getPrompt());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/nutrition-advice")
    @Operation(summary = "Get personalized nutrition advice")
    public ResponseEntity<ApiResponse<AIResponse>> getNutritionAdvice(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AIRequest request) {
        ApiResponse<AIResponse> response = aiService.getNutritionAdvice(
                userDetails.getUsername(), request.getPrompt());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/general-advice")
    @Operation(summary = "Get general fitness advice")
    public ResponseEntity<ApiResponse<AIResponse>> getGeneralAdvice(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AIRequest request) {
        ApiResponse<AIResponse> response = aiService.getGeneralFitnessAdvice(
                userDetails.getUsername(), request.getPrompt());
        return ResponseEntity.ok(response);
    }
}
