package com.fitness_application.recommendation.repository;

import com.fitness_application.recommendation.model.Recommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    Optional<Recommendation> findByIdAndUserId(Long id, Long userId);

    Page<Recommendation> findByUserIdAndIsActiveOrderByPriorityAscCreatedAtDesc(
            Long userId, Boolean isActive, Pageable pageable);

    Page<Recommendation> findByUserIdAndTypeAndIsActiveOrderByPriorityAscCreatedAtDesc(
            Long userId, String type, Boolean isActive, Pageable pageable);

    List<Recommendation> findByUserIdAndIsActiveAndIsSeen(Long userId, Boolean isActive, Boolean isSeen);

    @Query("SELECT COUNT(r) FROM Recommendation r WHERE r.userId = :userId AND r.isActive = :isActive")
    Long countByUserIdAndIsActive(@Param("userId") Long userId, @Param("isActive") Boolean isActive);

    @Query("SELECT r FROM Recommendation r WHERE r.userId = :userId AND r.confidenceScore >= :minScore AND r.isActive = true ORDER BY r.confidenceScore DESC, r.priority ASC")
    Page<Recommendation> findHighConfidenceRecommendations(
            @Param("userId") Long userId, 
            @Param("minScore") Double minScore, 
            Pageable pageable);

    void deleteByUserIdAndId(Long userId, Long id);
}
