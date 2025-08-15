package com.fitness_application.auth.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
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
    
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;
    
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum UserRole {
        USER, ADMIN;
        
        public boolean hasPermission(String permission) {
            return this == ADMIN || "READ".equals(permission);
        }
        
        public boolean canAccessResource(String resourceType, String operation) {
            return this == ADMIN || "READ".equals(operation);
        }
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
