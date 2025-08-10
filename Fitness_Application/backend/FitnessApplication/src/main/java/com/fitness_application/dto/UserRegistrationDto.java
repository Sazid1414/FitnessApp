package com.fitness_application.dto;

import com.fitness_application.domain.enums.Gender;
import com.fitness_application.domain.enums.ActivityLevel;
import com.fitness_application.domain.enums.FitnessGoal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    private Gender gender;
    
    @Positive(message = "Height must be positive")
    private Double height;
    
    @Positive(message = "Weight must be positive")
    private Double weight;
    
    private ActivityLevel activityLevel;
    private FitnessGoal fitnessGoal;
}
