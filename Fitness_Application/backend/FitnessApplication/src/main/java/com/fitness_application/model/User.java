package com.fitness_application.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fitness_application.domain.enums.ActivityLevel;
import com.fitness_application.domain.enums.FitnessGoal;
import com.fitness_application.domain.enums.Gender;
import com.fitness_application.domain.enums.UserRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    
    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Workout> workouts;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NutritionLog> nutritionLogs;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Goal> goals;
    
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
    
    // Domain business logic methods (following Clean Architecture)
    
    /**
     * Calculate BMR using Gender-specific formulas from domain enum
     * Domain logic delegated to Gender enum following SOLID principles
     */
    public double calculateBasalMetabolicRate() {
        if (height == null || weight == null || dateOfBirth == null || gender == null) {
            throw new IllegalStateException("Cannot calculate BMR without complete biometric data");
        }
        
        int age = getAge();
        return gender.calculateBMR(weight, height, age);
    }
    
    /**
     * Calculate daily calorie needs based on activity level
     * Combines domain logic from entity and enum
     */
    public double calculateDailyCalorieNeeds() {
        if (activityLevel == null) {
            throw new IllegalStateException("Activity level must be set to calculate calorie needs");
        }
        return activityLevel.calculateDailyCalories(calculateBasalMetabolicRate());
    }
    
    /**
     * Calculate target calories based on fitness goal
     * Business logic combining BMR, activity level, and goal adjustments
     */
    public double calculateTargetCalories() {
        if (fitnessGoal == null) {
            return calculateDailyCalorieNeeds();
        }
        return fitnessGoal.adjustCalories(calculateDailyCalorieNeeds());
    }
    
    /**
     * Check if user has specific permission (delegation to role)
     * Security domain logic
     */
    public boolean hasPermission(String permission) {
        return role.hasPermission(permission);
    }
    
    /**
     * Check if user can access resource (delegation to role)
     * Security domain logic
     */
    public boolean canAccessResource(String resourceType, String operation) {
        return role.canAccessResource(resourceType, operation);
    }
    
    /**
     * Check if user has complete profile for fitness calculations
     * Domain business logic
     */
    public boolean hasCompleteProfile() {
        return height != null && weight != null && dateOfBirth != null && 
               gender != null && activityLevel != null && fitnessGoal != null;
    }
    
    /**
     * Calculate age from date of birth
     * Domain utility method
     */
    public int getAge() {
        if (dateOfBirth == null) {
            throw new IllegalStateException("Date of birth not set");
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
}
