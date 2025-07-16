package com.fitness_application.dto;

import com.fitness_application.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private User.Gender gender;
    private Double height;
    private Double weight;
    private User.ActivityLevel activityLevel;
    private User.FitnessGoal fitnessGoal;
    private String profilePictureUrl;
    private User.Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
