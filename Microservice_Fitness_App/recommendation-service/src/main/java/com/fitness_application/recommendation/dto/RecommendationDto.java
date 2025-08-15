package com.fitness_application.recommendation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String description;
    private String content;
    private Double confidenceScore;
    private Integer priority;
    private Boolean isActive;
    private Boolean isSeen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
