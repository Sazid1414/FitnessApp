package com.fitness_application.domain.enums;

/**
 * Gender enumeration following Domain-Driven Design principles.
 * 
 * This enum represents the domain concept of gender and is independent
 * of any infrastructure concerns or specific entity implementations.
 * 
 * Benefits:
 * - Single Responsibility: Only handles gender-related logic
 * - Open/Closed: Can be extended without modifying existing entities
 * - Domain-centric: Pure domain concept without persistence annotations
 */
public enum Gender {
    MALE("Male"),
    FEMALE("Female"), 
    OTHER("Other"),
    PREFER_NOT_TO_SAY("Prefer not to say");
    
    private final String displayName;
    
    Gender(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Calculate BMR using gender-specific formulas
     * Mifflin-St Jeor Equation implementation
     * Domain business logic for metabolic calculations
     */
    public double calculateBMR(double weight, double height, int age) {
        return switch (this) {
            case MALE -> (10 * weight) + (6.25 * height) - (5 * age) + 5;
            case FEMALE -> (10 * weight) + (6.25 * height) - (5 * age) - 161;
            case OTHER, PREFER_NOT_TO_SAY -> {
                // Use average of male and female formulas for other genders
                double maleFormula = (10 * weight) + (6.25 * height) - (5 * age) + 5;
                double femaleFormula = (10 * weight) + (6.25 * height) - (5 * age) - 161;
                yield (maleFormula + femaleFormula) / 2;
            }
        };
    }
    
    /**
     * Business logic method - belongs in domain layer
     */
    public boolean requiresSpecialConsiderations() {
        return this == OTHER || this == PREFER_NOT_TO_SAY;
    }
}
