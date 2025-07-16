package com.fitness_application.ai;

import lombok.Data;

@Data
public class AIRequest {
    private String prompt;
    private String userProfile;
    private String requestType; // WORKOUT_PLAN, NUTRITION_ADVICE, GENERAL_ADVICE
}
