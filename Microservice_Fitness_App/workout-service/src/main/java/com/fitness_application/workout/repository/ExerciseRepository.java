package com.fitness_application.workout.repository;

import com.fitness_application.workout.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    
    List<Exercise> findByWorkoutId(Long workoutId);
    
    @Query("SELECT e FROM Exercise e WHERE e.workout.id = :workoutId ORDER BY e.id")
    List<Exercise> findByWorkoutIdOrderById(@Param("workoutId") Long workoutId);
    
    @Query("SELECT e FROM Exercise e WHERE e.exerciseType = :exerciseType")
    List<Exercise> findByExerciseType(@Param("exerciseType") String exerciseType);
    
    @Query("SELECT e FROM Exercise e WHERE e.workout.userId = :userId")
    List<Exercise> findByWorkoutUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(e) FROM Exercise e WHERE e.workout.id = :workoutId")
    long countByWorkoutId(@Param("workoutId") Long workoutId);
    
    void deleteByWorkoutId(Long workoutId);
}
