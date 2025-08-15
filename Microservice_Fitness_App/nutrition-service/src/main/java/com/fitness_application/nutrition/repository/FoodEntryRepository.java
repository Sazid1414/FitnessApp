package com.fitness_application.nutrition.repository;

import com.fitness_application.nutrition.model.FoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodEntryRepository extends JpaRepository<FoodEntry, Long> {
    
    List<FoodEntry> findByNutritionLogId(Long nutritionLogId);
    
    @Query("SELECT f FROM FoodEntry f WHERE f.nutritionLog.id = :nutritionLogId ORDER BY f.id")
    List<FoodEntry> findByNutritionLogIdOrderById(@Param("nutritionLogId") Long nutritionLogId);
    
    @Query("SELECT f FROM FoodEntry f WHERE f.nutritionLog.userId = :userId")
    List<FoodEntry> findByNutritionLogUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(f) FROM FoodEntry f WHERE f.nutritionLog.id = :nutritionLogId")
    long countByNutritionLogId(@Param("nutritionLogId") Long nutritionLogId);
    
    void deleteByNutritionLogId(Long nutritionLogId);
}
