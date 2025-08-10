package com.fitness_application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fitness_application.domain.enums.ActivityLevel;
import com.fitness_application.domain.enums.FitnessGoal;
import com.fitness_application.domain.enums.Gender;
import com.fitness_application.domain.enums.UserRole;

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
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
