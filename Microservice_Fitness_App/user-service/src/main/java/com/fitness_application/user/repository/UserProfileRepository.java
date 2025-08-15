package com.fitness_application.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitness_application.user.model.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
