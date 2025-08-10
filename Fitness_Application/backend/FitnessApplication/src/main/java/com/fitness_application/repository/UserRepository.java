package com.fitness_application.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fitness_application.domain.enums.UserRole;
import com.fitness_application.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:search% OR u.lastName LIKE %:search% OR u.email LIKE %:search%")
    Page<User> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    // Role-based queries
    long countByRole(UserRole role);
    
    // Status-based queries
    long countByEnabled(boolean enabled);
    
    // Legacy queries (for backward compatibility)
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'USER'")
    long countUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countActiveUsers();
}
