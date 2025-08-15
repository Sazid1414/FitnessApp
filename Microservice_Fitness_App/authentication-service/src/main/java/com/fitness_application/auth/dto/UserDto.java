package com.fitness_application.auth.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fitness_application.auth.model.User.ActivityLevel;
import com.fitness_application.auth.model.User.FitnessGoal;
import com.fitness_application.auth.model.User.Gender;
import com.fitness_application.auth.model.User.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private Double height;
    private Double weight;
    private ActivityLevel activityLevel;
    private FitnessGoal fitnessGoal;
    private String profilePictureUrl;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
