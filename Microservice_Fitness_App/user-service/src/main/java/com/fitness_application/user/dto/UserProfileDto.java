package com.fitness_application.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fitness_application.user.model.UserProfile.ActivityLevel;
import com.fitness_application.user.model.UserProfile.FitnessGoal;
import com.fitness_application.user.model.UserProfile.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long userId;
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
    private String timeZone;
    private String language;
    private Boolean emailNotifications;
    private Boolean pushNotifications;
    private String measurementUnit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
