package com.fitness_application.repository;

import com.fitness_application.model.Workout;
import com.fitness_application.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    
    List<Workout> findByUserOrderByWorkoutDateDesc(User user);
    
    Page<Workout> findByUserOrderByWorkoutDateDesc(User user, Pageable pageable);
    
    List<Workout> findByUserAndWorkoutDateBetween(User user, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT w FROM Workout w WHERE w.user = :user AND w.completed = :completed ORDER BY w.workoutDate DESC")
    List<Workout> findByUserAndCompleted(@Param("user") User user, @Param("completed") boolean completed);
    
    @Query("SELECT COUNT(w) FROM Workout w WHERE w.user = :user AND w.completed = true")
    long countCompletedWorkoutsByUser(@Param("user") User user);
    
    @Query("SELECT SUM(w.caloriesBurned) FROM Workout w WHERE w.user = :user AND w.completed = true AND w.workoutDate BETWEEN :start AND :end")
    Integer sumCaloriesBurnedByUserBetweenDates(@Param("user") User user, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
