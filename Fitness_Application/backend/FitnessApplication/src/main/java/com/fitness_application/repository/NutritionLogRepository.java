package com.fitness_application.repository;

import com.fitness_application.model.NutritionLog;
import com.fitness_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NutritionLogRepository extends JpaRepository<NutritionLog, Long> {
    
    List<NutritionLog> findByUserAndLogDateOrderByMealType(User user, LocalDate logDate);
    
    Optional<NutritionLog> findByUserAndLogDateAndMealType(User user, LocalDate logDate, NutritionLog.MealType mealType);
    
    List<NutritionLog> findByUserAndLogDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(n.totalCalories) FROM NutritionLog n WHERE n.user = :user AND n.logDate = :date")
    Integer sumCaloriesByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);
    
    @Query("SELECT SUM(n.totalCalories) FROM NutritionLog n WHERE n.user = :user AND n.logDate BETWEEN :startDate AND :endDate")
    Integer sumCaloriesByUserBetweenDates(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
