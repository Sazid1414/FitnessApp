package com.fitness_application.domain.enums;

import java.util.Set;

/**
 * User Role enumeration following security domain principles.
 * 
 * Represents different user roles and their permissions in the system.
 * Follows SOLID principles and clean architecture patterns.
 * 
 * Security Domain Considerations:
 * - Principle of Least Privilege
 * - Role-Based Access Control (RBAC)
 * - Separation of Concerns
 * - Defensive Programming
 */
public enum UserRole {
    USER("User", Set.of(
        "PROFILE_READ", "PROFILE_WRITE",
        "WORKOUT_READ", "WORKOUT_WRITE", "WORKOUT_DELETE",
        "NUTRITION_READ", "NUTRITION_WRITE", "NUTRITION_DELETE",
        "GOAL_READ", "GOAL_WRITE", "GOAL_DELETE",
        "AI_CONSULTATION"
    )),
    
    ADMIN("Administrator", Set.of(
        // All user permissions
        "PROFILE_READ", "PROFILE_WRITE",
        "WORKOUT_READ", "WORKOUT_WRITE", "WORKOUT_DELETE",
        "NUTRITION_READ", "NUTRITION_WRITE", "NUTRITION_DELETE",
        "GOAL_READ", "GOAL_WRITE", "GOAL_DELETE",
        "AI_CONSULTATION",
        // Admin-specific permissions
        "USER_MANAGEMENT", "USER_READ_ALL", "USER_WRITE_ALL", "USER_DELETE",
        "ROLE_MANAGEMENT", "SYSTEM_CONFIG", "ANALYTICS_READ",
        "AUDIT_LOG_READ", "SYSTEM_HEALTH"
    )),
    
    MODERATOR("Moderator", Set.of(
        "PROFILE_READ", "PROFILE_WRITE",
        "WORKOUT_READ", "WORKOUT_WRITE",
        "USER_READ_ALL", "CONTENT_MODERATION"
    ));
    
    private final String displayName;
    private final Set<String> permissions;
    
    UserRole(String displayName, Set<String> permissions) {
        this.displayName = displayName;
        this.permissions = Set.copyOf(permissions); // Immutable copy for security
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Set<String> getPermissions() {
        return permissions; // Already immutable
    }
    
    /**
     * Security business logic: Check if role has specific permission
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    /**
     * Security business logic: Check if role can access resource
     */
    public boolean canAccessResource(String resourceType, String operation) {
        String permission = resourceType.toUpperCase() + "_" + operation.toUpperCase();
        return hasPermission(permission);
    }
    
    /**
     * Security business logic: Check if role is administrative
     */
    public boolean isAdministrative() {
        return this == ADMIN || this == MODERATOR;
    }
    
    /**
     * Security business logic: Check if role can manage users
     */
    public boolean canManageUsers() {
        return hasPermission("USER_MANAGEMENT");
    }
    
    /**
     * Security business logic: Get role hierarchy level (for role-based decisions)
     */
    public int getHierarchyLevel() {
        return switch (this) {
            case USER -> 1;
            case MODERATOR -> 2;
            case ADMIN -> 3;
        };
    }
    
    /**
     * Security business logic: Check if this role can assign another role
     */
    public boolean canAssignRole(UserRole targetRole) {
        // Only roles with higher hierarchy can assign lower roles
        return this.getHierarchyLevel() > targetRole.getHierarchyLevel();
    }
}
