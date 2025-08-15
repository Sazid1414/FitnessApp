package com.fitness_application.user.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    
    @Id
    private Long userId; // Same as user ID from auth service
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    private Double height; // in cm
    private Double weight; // in kg
    
    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;
    
    @Enumerated(EnumType.STRING)
    private FitnessGoal fitnessGoal;
    
    private String profilePictureUrl;
    
    // Preferences
    private String timeZone = "UTC";
    private String language = "en";
    private Boolean emailNotifications = true;
    private Boolean pushNotifications = true;
    private String measurementUnit = "METRIC"; // METRIC or IMPERIAL
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Calculate age from date of birth
    public int getAge() {
        if (dateOfBirth == null) {
            throw new IllegalStateException("Date of birth not set");
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
    
    // Check if user has complete profile for fitness calculations
    public boolean hasCompleteProfile() {
        return height != null && weight != null && dateOfBirth != null && 
               gender != null && activityLevel != null && fitnessGoal != null;
    }
    
    // Business logic for BMR calculation
    public double calculateBasalMetabolicRate() {
        if (!hasCompleteProfile()) {
            throw new IllegalStateException("Cannot calculate BMR without complete biometric data");
        }
        
        int age = getAge();
        return gender.calculateBMR(weight, height, age);
    }
    
    public enum Gender {
        MALE, FEMALE, OTHER;
        
        public double calculateBMR(double weight, double height, int age) {
            return switch (this) {
                case MALE -> 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
                case FEMALE -> 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
                case OTHER -> 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
            };
        }
    }
    
    public enum ActivityLevel {
        SEDENTARY(1.2),
        LIGHTLY_ACTIVE(1.375),
        MODERATELY_ACTIVE(1.55),
        VERY_ACTIVE(1.725),
        EXTREMELY_ACTIVE(1.9);
        
        private final double multiplier;
        
        ActivityLevel(double multiplier) {
            this.multiplier = multiplier;
        }
        
        public double calculateDailyCalories(double bmr) {
            return bmr * multiplier;
        }
    }
    
    public enum FitnessGoal {
        WEIGHT_LOSS(-500),
        MAINTAIN_WEIGHT(0),
        WEIGHT_GAIN(500),
        MUSCLE_GAIN(300),
        ENDURANCE(200);
        
        private final int calorieAdjustment;
        
        FitnessGoal(int calorieAdjustment) {
            this.calorieAdjustment = calorieAdjustment;
        }
        
        public double adjustCalories(double dailyCalories) {
            return dailyCalories + calorieAdjustment;
        }
    }
}
