package com.fitness_application.nutrition.repository;

import com.fitness_application.nutrition.model.NutritionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NutritionLogRepository extends JpaRepository<NutritionLog, Long> {
    
    List<NutritionLog> findByUserIdOrderByLogDateDesc(Long userId);
    
    Page<NutritionLog> findByUserIdOrderByLogDateDesc(Long userId, Pageable pageable);
    
    List<NutritionLog> findByUserIdAndLogDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    Optional<NutritionLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);
    
    @Query("SELECT n FROM NutritionLog n WHERE n.userId = :userId AND n.logDate = :logDate")
    Optional<NutritionLog> findByUserAndDate(@Param("userId") Long userId, @Param("logDate") LocalDate logDate);
    
    @Query("SELECT SUM(n.totalCalories) FROM NutritionLog n WHERE n.userId = :userId AND n.logDate BETWEEN :startDate AND :endDate")
    Double totalCaloriesByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(n) FROM NutritionLog n WHERE n.userId = :userId")
    long countNutritionLogsByUser(@Param("userId") Long userId);
    
    Optional<NutritionLog> findByIdAndUserId(Long id, Long userId);
    
    void deleteByIdAndUserId(Long id, Long userId);
}
