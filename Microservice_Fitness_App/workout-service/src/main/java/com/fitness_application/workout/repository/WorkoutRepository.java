package com.fitness_application.workout.repository;

import com.fitness_application.workout.model.Workout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    
    List<Workout> findByUserIdOrderByWorkoutDateDesc(Long userId);
    
    Page<Workout> findByUserIdOrderByWorkoutDateDesc(Long userId, Pageable pageable);
    
    List<Workout> findByUserIdAndWorkoutDateBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT w FROM Workout w WHERE w.userId = :userId AND w.completed = :completed ORDER BY w.workoutDate DESC")
    List<Workout> findByUserIdAndCompleted(@Param("userId") Long userId, @Param("completed") boolean completed);
    
    @Query("SELECT COUNT(w) FROM Workout w WHERE w.userId = :userId AND w.completed = true")
    long countCompletedWorkoutsByUser(@Param("userId") Long userId);
    
    @Query("SELECT SUM(w.caloriesBurned) FROM Workout w WHERE w.userId = :userId AND w.completed = true AND w.workoutDate BETWEEN :start AND :end")
    Double totalCaloriesBurnedByUserAndDateRange(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    Optional<Workout> findByIdAndUserId(Long id, Long userId);
    
    void deleteByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT COUNT(w) FROM Workout w WHERE w.userId = :userId")
    long countWorkoutsByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(w) FROM Workout w WHERE w.completed = :completed")
    long countByCompleted(@Param("completed") boolean completed);
}
