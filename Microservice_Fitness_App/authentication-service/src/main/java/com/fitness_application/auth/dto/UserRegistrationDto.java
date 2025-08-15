package com.fitness_application.auth.dto;

import java.time.LocalDate;

import com.fitness_application.auth.model.User.ActivityLevel;
import com.fitness_application.auth.model.User.FitnessGoal;
import com.fitness_application.auth.model.User.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password should be at least 6 characters")
    private String password;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    private LocalDate dateOfBirth;
    private Gender gender;
    private Double height; // in cm
    private Double weight; // in kg
    private ActivityLevel activityLevel;
    private FitnessGoal fitnessGoal;
    private String profilePictureUrl;
}
