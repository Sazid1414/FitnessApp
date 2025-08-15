package com.fitness_application.recommendation.service;

import com.fitness_application.recommendation.dto.ApiResponse;
import com.fitness_application.recommendation.dto.RecommendationDto;
import com.fitness_application.recommendation.model.Recommendation;
import com.fitness_application.recommendation.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final ModelMapper modelMapper;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public ApiResponse<RecommendationDto> createRecommendation(Long userId, RecommendationDto recommendationDto) {
        try {
            log.info("Creating recommendation for user: {}", userId);

            Recommendation recommendation = modelMapper.map(recommendationDto, Recommendation.class);
            recommendation.setUserId(userId);
            recommendation.setCreatedAt(LocalDateTime.now());
            recommendation.setUpdatedAt(LocalDateTime.now());

            if (recommendation.getIsActive() == null) {
                recommendation.setIsActive(true);
            }
            if (recommendation.getIsSeen() == null) {
                recommendation.setIsSeen(false);
            }
            if (recommendation.getPriority() == null) {
                recommendation.setPriority(1);
            }

            Recommendation savedRecommendation = recommendationRepository.save(recommendation);
            RecommendationDto responseDto = modelMapper.map(savedRecommendation, RecommendationDto.class);

            log.info("Successfully created recommendation with ID: {} for user: {}", 
                    savedRecommendation.getId(), userId);

            return new ApiResponse<>(
                    true,
                    "Recommendation created successfully",
                    responseDto
            );

        } catch (Exception e) {
            log.error("Error creating recommendation for user {}: {}", userId, e.getMessage(), e);
            return new ApiResponse<>(
                    false,
                    "Error creating recommendation: " + e.getMessage(),
                    null
            );
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<RecommendationDto>> getRecommendationsForUser(Long userId, int page, int size) {
        try {
            log.info("Fetching recommendations for user: {}, page: {}, size: {}", userId, page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<Recommendation> recommendationsPage = recommendationRepository
                    .findByUserIdAndIsActiveOrderByPriorityAscCreatedAtDesc(userId, true, pageable);

            List<RecommendationDto> recommendationDtos = recommendationsPage.getContent().stream()
                    .map(recommendation -> modelMapper.map(recommendation, RecommendationDto.class))
                    .collect(Collectors.toList());

            log.info("Successfully fetched {} recommendations for user: {}", recommendationDtos.size(), userId);

            return new ApiResponse<>(
                    true,
                    "Recommendations retrieved successfully",
                    recommendationDtos
            );

        } catch (Exception e) {
            log.error("Error fetching recommendations for user {}: {}", userId, e.getMessage(), e);
            return new ApiResponse<>(
                    false,
                    "Error fetching recommendations: " + e.getMessage(),
                    null
            );
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<RecommendationDto> getRecommendationById(Long userId, Long recommendationId) {
        try {
            log.info("Fetching recommendation {} for user: {}", recommendationId, userId);

            Optional<Recommendation> recommendationOpt = recommendationRepository
                    .findByIdAndUserId(recommendationId, userId);

            if (recommendationOpt.isEmpty()) {
                return new ApiResponse<>(
                        false,
                        "Recommendation not found",
                        null
                );
            }

            RecommendationDto recommendationDto = modelMapper.map(recommendationOpt.get(), RecommendationDto.class);

            log.info("Successfully fetched recommendation {} for user: {}", recommendationId, userId);

            return new ApiResponse<>(
                    true,
                    "Recommendation retrieved successfully",
                    recommendationDto
            );

        } catch (Exception e) {
            log.error("Error fetching recommendation {} for user {}: {}", recommendationId, userId, e.getMessage(), e);
            return new ApiResponse<>(
                    false,
                    "Error fetching recommendation: " + e.getMessage(),
                    null
            );
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<RecommendationDto>> getRecommendationsByType(Long userId, String type, int page, int size) {
        try {
            log.info("Fetching {} recommendations for user: {}, page: {}, size: {}", type, userId, page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<Recommendation> recommendationsPage = recommendationRepository
                    .findByUserIdAndTypeAndIsActiveOrderByPriorityAscCreatedAtDesc(userId, type, true, pageable);

            List<RecommendationDto> recommendationDtos = recommendationsPage.getContent().stream()
                    .map(recommendation -> modelMapper.map(recommendation, RecommendationDto.class))
                    .collect(Collectors.toList());

            log.info("Successfully fetched {} {} recommendations for user: {}", 
                    recommendationDtos.size(), type, userId);

            return new ApiResponse<>(
                    true,
                    "Recommendations retrieved successfully",
                    recommendationDtos
            );

        } catch (Exception e) {
            log.error("Error fetching {} recommendations for user {}: {}", type, userId, e.getMessage(), e);
            return new ApiResponse<>(
                    false,
                    "Error fetching recommendations: " + e.getMessage(),
                    null
            );
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ApiResponse<RecommendationDto> updateRecommendation(Long userId, Long recommendationId, 
                                                              RecommendationDto recommendationDto) {
        try {
            log.info("Updating recommendation {} for user: {}", recommendationId, userId);

            Optional<Recommendation> recommendationOpt = recommendationRepository
                    .findByIdAndUserId(recommendationId, userId);

            if (recommendationOpt.isEmpty()) {
                return new ApiResponse<>(
                        false,
                        "Recommendation not found",
                        null
                );
            }

            Recommendation recommendation = recommendationOpt.get();

            // Update fields if provided
            if (recommendationDto.getTitle() != null) {
                recommendation.setTitle(recommendationDto.getTitle());
            }
            if (recommendationDto.getDescription() != null) {
                recommendation.setDescription(recommendationDto.getDescription());
            }
            if (recommendationDto.getContent() != null) {
                recommendation.setContent(recommendationDto.getContent());
            }
            if (recommendationDto.getConfidenceScore() != null) {
                recommendation.setConfidenceScore(recommendationDto.getConfidenceScore());
            }
            if (recommendationDto.getPriority() != null) {
                recommendation.setPriority(recommendationDto.getPriority());
            }
            if (recommendationDto.getIsActive() != null) {
                recommendation.setIsActive(recommendationDto.getIsActive());
            }

            recommendation.setUpdatedAt(LocalDateTime.now());

            Recommendation savedRecommendation = recommendationRepository.save(recommendation);
            RecommendationDto responseDto = modelMapper.map(savedRecommendation, RecommendationDto.class);

            log.info("Successfully updated recommendation {} for user: {}", recommendationId, userId);

            return new ApiResponse<>(
                    true,
                    "Recommendation updated successfully",
                    responseDto
            );

        } catch (Exception e) {
            log.error("Error updating recommendation {} for user {}: {}", recommendationId, userId, e.getMessage(), e);
            return new ApiResponse<>(
                    false,
                    "Error updating recommendation: " + e.getMessage(),
                    null
            );
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ApiResponse<Boolean> deleteRecommendation(Long userId, Long recommendationId) {
        try {
            log.info("Deleting recommendation {} for user: {}", recommendationId, userId);

            Optional<Recommendation> recommendationOpt = recommendationRepository
                    .findByIdAndUserId(recommendationId, userId);

            if (recommendationOpt.isEmpty()) {
                return new ApiResponse<>(
                        false,
                        "Recommendation not found",
                        false
                );
            }

            recommendationRepository.delete(recommendationOpt.get());

            log.info("Successfully deleted recommendation {} for user: {}", recommendationId, userId);

            return new ApiResponse<>(
                    true,
                    "Recommendation deleted successfully",
                    true
            );

        } catch (Exception e) {
            log.error("Error deleting recommendation {} for user {}: {}", recommendationId, userId, e.getMessage(), e);
            return new ApiResponse<>(
                    false,
                    "Error deleting recommendation: " + e.getMessage(),
                    false
            );
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ApiResponse<Boolean> markRecommendationAsSeen(Long userId, Long recommendationId) {
        try {
            log.info("Marking recommendation {} as seen for user: {}", recommendationId, userId);

            Optional<Recommendation> recommendationOpt = recommendationRepository
                    .findByIdAndUserId(recommendationId, userId);

            if (recommendationOpt.isEmpty()) {
                return new ApiResponse<>(
                        false,
                        "Recommendation not found",
                        false
                );
            }

            Recommendation recommendation = recommendationOpt.get();
            recommendation.setIsSeen(true);
            recommendation.setUpdatedAt(LocalDateTime.now());

            recommendationRepository.save(recommendation);

            log.info("Successfully marked recommendation {} as seen for user: {}", recommendationId, userId);

            return new ApiResponse<>(
                    true,
                    "Recommendation marked as seen successfully",
                    true
            );

        } catch (Exception e) {
            log.error("Error marking recommendation {} as seen for user {}: {}", recommendationId, userId, e.getMessage(), e);
            return new ApiResponse<>(
                    false,
                    "Error marking recommendation as seen: " + e.getMessage(),
                    false
            );
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ApiResponse<Boolean> deactivateRecommendation(Long userId, Long recommendationId) {
        try {
            log.info("Deactivating recommendation {} for user: {}", recommendationId, userId);

            Optional<Recommendation> recommendationOpt = recommendationRepository
                    .findByIdAndUserId(recommendationId, userId);

            if (recommendationOpt.isEmpty()) {
                return new ApiResponse<>(
                        false,
                        "Recommendation not found",
                        false
                );
            }

            Recommendation recommendation = recommendationOpt.get();
            recommendation.setIsActive(false);
            recommendation.setUpdatedAt(LocalDateTime.now());

            recommendationRepository.save(recommendation);

            log.info("Successfully deactivated recommendation {} for user: {}", recommendationId, userId);

            return new ApiResponse<>(
                    true,
                    "Recommendation deactivated successfully",
                    true
            );

        } catch (Exception e) {
            log.error("Error deactivating recommendation {} for user {}: {}", recommendationId, userId, e.getMessage(), e);
            return new ApiResponse<>(
                    false,
                    "Error deactivating recommendation: " + e.getMessage(),
                    false
            );
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> getRecommendationCount(Long userId) {
        try {
            log.info("Getting recommendation count for user: {}", userId);

            Long count = recommendationRepository.countByUserIdAndIsActive(userId, true);

            log.info("Successfully retrieved recommendation count {} for user: {}", count, userId);

            return new ApiResponse<>(
                    true,
                    "Recommendation count retrieved successfully",
                    count
            );

        } catch (Exception e) {
            log.error("Error getting recommendation count for user {}: {}", userId, e.getMessage(), e);
            return new ApiResponse<>(
                    false,
                    "Error getting recommendation count: " + e.getMessage(),
                    null
            );
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<RecommendationDto>> getHighConfidenceRecommendations(Long userId, Double minScore, 
                                                                                 int page, int size) {
        try {
            log.info("Fetching high confidence recommendations for user: {} with min score: {}", userId, minScore);

            Pageable pageable = PageRequest.of(page, size);
            Page<Recommendation> recommendationsPage = recommendationRepository
                    .findHighConfidenceRecommendations(userId, minScore, pageable);

            List<RecommendationDto> recommendationDtos = recommendationsPage.getContent().stream()
                    .map(recommendation -> modelMapper.map(recommendation, RecommendationDto.class))
                    .collect(Collectors.toList());

            log.info("Successfully fetched {} high confidence recommendations for user: {}", 
                    recommendationDtos.size(), userId);

            return new ApiResponse<>(
                    true,
                    "High confidence recommendations retrieved successfully",
                    recommendationDtos
            );

        } catch (Exception e) {
            log.error("Error fetching high confidence recommendations for user {}: {}", userId, e.getMessage(), e);
            return new ApiResponse<>(
                    false,
                    "Error fetching high confidence recommendations: " + e.getMessage(),
                    null
            );
        }
    }
}
