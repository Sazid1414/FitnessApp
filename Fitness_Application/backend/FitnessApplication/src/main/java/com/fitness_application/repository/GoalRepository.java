package com.fitness_application.repository;

import com.fitness_application.model.Goal;
import com.fitness_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    
    List<Goal> findByUserOrderByCreatedAtDesc(User user);
    
    List<Goal> findByUserAndStatus(User user, Goal.GoalStatus status);
    
    List<Goal> findByUserAndType(User user, Goal.GoalType type);
    
    @Query("SELECT COUNT(g) FROM Goal g WHERE g.user = :user AND g.status = 'COMPLETED'")
    long countCompletedGoalsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(g) FROM Goal g WHERE g.user = :user AND g.status = 'ACTIVE'")
    long countActiveGoalsByUser(@Param("user") User user);
}
