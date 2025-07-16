package com.fitness_application.ai;

import com.fitness_application.dto.ApiResponse;
import com.fitness_application.exception.ResourceNotFoundException;
import com.fitness_application.model.User;
import com.fitness_application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {
    
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    
    @Value("${ai.gemini.api.key}")
    private String geminiApiKey;
    
    @Value("${ai.gemini.api.url}")
    private String geminiApiUrl;
    
    public ApiResponse<AIResponse> getWorkoutRecommendation(String userEmail, String prompt) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String userProfile = buildUserProfile(user);
        String fullPrompt = String.format(
            "As a fitness AI assistant, provide a personalized workout recommendation based on the following user profile:\n%s\n\nUser's specific request: %s\n\nProvide a detailed workout plan with exercises, sets, reps, and estimated duration.",
            userProfile, prompt
        );
        
        AIResponse aiResponse = callGeminiAPI(fullPrompt, "WORKOUT_PLAN");
        return ApiResponse.success("Workout recommendation generated", aiResponse);
    }
    
    public ApiResponse<AIResponse> getNutritionAdvice(String userEmail, String prompt) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String userProfile = buildUserProfile(user);
        String fullPrompt = String.format(
            "As a nutrition AI assistant, provide personalized nutrition advice based on the following user profile:\n%s\n\nUser's specific question: %s\n\nProvide detailed nutritional guidance, meal suggestions, and calorie recommendations.",
            userProfile, prompt
        );
        
        AIResponse aiResponse = callGeminiAPI(fullPrompt, "NUTRITION_ADVICE");
        return ApiResponse.success("Nutrition advice generated", aiResponse);
    }
    
    public ApiResponse<AIResponse> getGeneralFitnessAdvice(String userEmail, String prompt) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String userProfile = buildUserProfile(user);
        String fullPrompt = String.format(
            "As a general fitness AI assistant, provide helpful advice based on the following user profile:\n%s\n\nUser's question: %s\n\nProvide practical, motivational, and safe fitness advice.",
            userProfile, prompt
        );
        
        AIResponse aiResponse = callGeminiAPI(fullPrompt, "GENERAL_ADVICE");
        return ApiResponse.success("Fitness advice generated", aiResponse);
    }
    
    private String buildUserProfile(User user) {
        StringBuilder profile = new StringBuilder();
        profile.append("User Profile:\n");
        profile.append("- Age: ").append(calculateAge(user)).append(" years\n");
        profile.append("- Gender: ").append(user.getGender() != null ? user.getGender() : "Not specified").append("\n");
        profile.append("- Height: ").append(user.getHeight() != null ? user.getHeight() + " cm" : "Not specified").append("\n");
        profile.append("- Weight: ").append(user.getWeight() != null ? user.getWeight() + " kg" : "Not specified").append("\n");
        profile.append("- Activity Level: ").append(user.getActivityLevel() != null ? user.getActivityLevel() : "Not specified").append("\n");
        profile.append("- Fitness Goal: ").append(user.getFitnessGoal() != null ? user.getFitnessGoal() : "Not specified").append("\n");
        
        if (user.getHeight() != null && user.getWeight() != null) {
            double bmi = calculateBMI(user);
            profile.append("- BMI: ").append(String.format("%.1f", bmi)).append("\n");
        }
        
        return profile.toString();
    }
    
    private int calculateAge(User user) {
        if (user.getDateOfBirth() != null) {
            return java.time.Period.between(user.getDateOfBirth(), java.time.LocalDate.now()).getYears();
        }
        return 0;
    }
    
    private double calculateBMI(User user) {
        if (user.getHeight() != null && user.getWeight() != null) {
            double heightInMeters = user.getHeight() / 100.0;
            return user.getWeight() / (heightInMeters * heightInMeters);
        }
        return 0.0;
    }
    
    private AIResponse callGeminiAPI(String prompt, String type) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", geminiApiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            Map<String, Object> parts = new HashMap<>();
            parts.put("text", prompt);
            contents.put("parts", List.of(parts));
            requestBody.put("contents", List.of(contents));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // For development/demo purposes, if no API key is provided, return a mock response
            if (geminiApiKey == null || geminiApiKey.equals("your-gemini-api-key")) {
                return createMockResponse(type);
            }
            
            // Uncomment below for actual API call
            /*
            ResponseEntity<Map> response = restTemplate.exchange(
                geminiApiUrl + "?key=" + geminiApiKey,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            // Parse the response from Gemini API
            Map<String, Object> responseBody = response.getBody();
            // Extract the text from the response structure
            String responseText = extractTextFromGeminiResponse(responseBody);
            */
            
            // For now, return mock response
            return createMockResponse(type);
            
        } catch (Exception e) {
            log.error("Error calling Gemini API: ", e);
            AIResponse errorResponse = new AIResponse();
            errorResponse.setResponse("Sorry, I'm unable to provide recommendations at the moment. Please try again later.");
            errorResponse.setType(type);
            errorResponse.setSuccess(false);
            return errorResponse;
        }
    }
    
    private AIResponse createMockResponse(String type) {
        AIResponse response = new AIResponse();
        response.setType(type);
        response.setSuccess(true);
        
        switch (type) {
            case "WORKOUT_PLAN":
                response.setResponse("Based on your profile, here's a personalized workout plan:\n\n**Day 1: Upper Body Strength**\n- Push-ups: 3 sets of 10-15 reps\n- Dumbbell rows: 3 sets of 10-12 reps\n- Shoulder press: 3 sets of 8-10 reps\n- Planks: 3 sets of 30-60 seconds\n\n**Day 2: Cardio**\n- 30 minutes of moderate-intensity cardio\n- Options: brisk walking, cycling, or swimming\n\n**Day 3: Lower Body Strength**\n- Squats: 3 sets of 12-15 reps\n- Lunges: 3 sets of 10 per leg\n- Calf raises: 3 sets of 15-20 reps\n- Glute bridges: 3 sets of 12-15 reps\n\nRemember to warm up before exercising and cool down afterward!");
                break;
            case "NUTRITION_ADVICE":
                response.setResponse("Based on your fitness goals, here's personalized nutrition advice:\n\n**Daily Nutrition Guidelines:**\n- Protein: 1.2-1.6g per kg of body weight\n- Carbs: 45-65% of total calories\n- Fats: 20-35% of total calories\n- Water: At least 8-10 glasses per day\n\n**Meal Suggestions:**\n- Breakfast: Oatmeal with berries and nuts\n- Lunch: Grilled chicken salad with quinoa\n- Dinner: Baked salmon with vegetables\n- Snacks: Greek yogurt, fruits, or nuts\n\n**Tips:**\n- Eat protein within 30 minutes after workouts\n- Include colorful vegetables in every meal\n- Avoid processed foods and excessive sugar");
                break;
            case "GENERAL_ADVICE":
                response.setResponse("Here's some general fitness advice tailored to your profile:\n\n**Key Tips for Success:**\n1. **Consistency is key** - Aim for 150 minutes of moderate exercise per week\n2. **Listen to your body** - Rest when you need it\n3. **Set realistic goals** - Start small and gradually increase intensity\n4. **Stay hydrated** - Drink water before, during, and after workouts\n5. **Get adequate sleep** - 7-9 hours for optimal recovery\n\n**Motivation Tips:**\n- Track your progress with photos and measurements\n- Find a workout buddy for accountability\n- Celebrate small victories\n- Focus on how exercise makes you feel, not just appearance\n\nRemember: Fitness is a journey, not a destination. Be patient with yourself!");
                break;
            default:
                response.setResponse("I'm here to help with your fitness journey! Feel free to ask me about workouts, nutrition, or general fitness advice.");
        }
        
        return response;
    }
}
