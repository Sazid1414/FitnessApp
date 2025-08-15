package com.fitness_application.recommendation.service;

import com.fitness_application.recommendation.dto.ApiResponse;
import com.fitness_application.recommendation.dto.RecommendationDto;
import com.fitness_application.recommendation.model.Recommendation;
import com.fitness_application.recommendation.repository.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    private Recommendation recommendation;
    private RecommendationDto recommendationDto;

    @BeforeEach
    void setUp() {
        recommendation = new Recommendation();
        recommendation.setId(1L);
        recommendation.setUserId(100L);
        recommendation.setType("WORKOUT");
        recommendation.setTitle("Morning Cardio Workout");
        recommendation.setDescription("A 30-minute cardio workout to start your day");
        recommendation.setContent("{\"exercises\":[\"running\",\"jumping_jacks\"]}");
        recommendation.setConfidenceScore(0.85);
        recommendation.setPriority(1);
        recommendation.setIsActive(true);
        recommendation.setCreatedAt(LocalDateTime.now());
        recommendation.setUpdatedAt(LocalDateTime.now());

        recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setUserId(100L);
        recommendationDto.setType("WORKOUT");
        recommendationDto.setTitle("Morning Cardio Workout");
        recommendationDto.setDescription("A 30-minute cardio workout to start your day");
        recommendationDto.setContent("{\"exercises\":[\"running\",\"jumping_jacks\"]}");
        recommendationDto.setConfidenceScore(0.85);
        recommendationDto.setPriority(1);
        recommendationDto.setIsActive(true);
        recommendationDto.setCreatedAt(LocalDateTime.now());
        recommendationDto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateRecommendationSuccess() {
        // Arrange
        when(modelMapper.map(recommendationDto, Recommendation.class)).thenReturn(recommendation);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(modelMapper.map(recommendation, RecommendationDto.class)).thenReturn(recommendationDto);

        // Act
        ApiResponse<RecommendationDto> response = recommendationService.createRecommendation(100L, recommendationDto);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(recommendationDto, response.getData());
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void testGetRecommendationsForUserSuccess() {
        // Arrange
        List<Recommendation> recommendations = List.of(recommendation);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        when(recommendationRepository.findByUserIdAndIsActiveOrderByPriorityAscCreatedAtDesc(
                eq(100L), eq(true), any(Pageable.class))).thenReturn(recommendationPage);
        when(modelMapper.map(recommendation, RecommendationDto.class)).thenReturn(recommendationDto);

        // Act
        ApiResponse<List<RecommendationDto>> response = recommendationService.getRecommendationsForUser(100L, 0, 10);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        assertEquals(recommendationDto, response.getData().get(0));
        verify(recommendationRepository).findByUserIdAndIsActiveOrderByPriorityAscCreatedAtDesc(
                eq(100L), eq(true), any(Pageable.class));
    }

    @Test
    void testGetRecommendationByIdSuccess() {
        // Arrange
        when(recommendationRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(recommendation));
        when(modelMapper.map(recommendation, RecommendationDto.class)).thenReturn(recommendationDto);

        // Act
        ApiResponse<RecommendationDto> response = recommendationService.getRecommendationById(100L, 1L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(recommendationDto, response.getData());
        verify(recommendationRepository).findByIdAndUserId(1L, 100L);
    }

    @Test
    void testGetRecommendationByIdNotFound() {
        // Arrange
        when(recommendationRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<RecommendationDto> response = recommendationService.getRecommendationById(100L, 1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Recommendation not found", response.getMessage());
        verify(recommendationRepository).findByIdAndUserId(1L, 100L);
    }

    @Test
    void testGetRecommendationsByTypeSuccess() {
        // Arrange
        List<Recommendation> recommendations = List.of(recommendation);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        when(recommendationRepository.findByUserIdAndTypeAndIsActiveOrderByPriorityAscCreatedAtDesc(
                eq(100L), eq("WORKOUT"), eq(true), any(Pageable.class))).thenReturn(recommendationPage);
        when(modelMapper.map(recommendation, RecommendationDto.class)).thenReturn(recommendationDto);

        // Act
        ApiResponse<List<RecommendationDto>> response = recommendationService.getRecommendationsByType(
                100L, "WORKOUT", 0, 10);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        assertEquals(recommendationDto, response.getData().get(0));
        verify(recommendationRepository).findByUserIdAndTypeAndIsActiveOrderByPriorityAscCreatedAtDesc(
                eq(100L), eq("WORKOUT"), eq(true), any(Pageable.class));
    }

    @Test
    void testUpdateRecommendationSuccess() {
        // Arrange
        when(recommendationRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(recommendation));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(modelMapper.map(recommendation, RecommendationDto.class)).thenReturn(recommendationDto);

        recommendationDto.setTitle("Updated Workout");

        // Act
        ApiResponse<RecommendationDto> response = recommendationService.updateRecommendation(100L, 1L, recommendationDto);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(recommendationDto, response.getData());
        verify(recommendationRepository).findByIdAndUserId(1L, 100L);
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void testDeleteRecommendationSuccess() {
        // Arrange
        when(recommendationRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(recommendation));

        // Act
        ApiResponse<Boolean> response = recommendationService.deleteRecommendation(100L, 1L);

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData());
        verify(recommendationRepository).findByIdAndUserId(1L, 100L);
        verify(recommendationRepository).delete(recommendation);
    }

    @Test
    void testMarkRecommendationAsSeenSuccess() {
        // Arrange
        when(recommendationRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(recommendation));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        // Act
        ApiResponse<Boolean> response = recommendationService.markRecommendationAsSeen(100L, 1L);

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData());
        verify(recommendationRepository).findByIdAndUserId(1L, 100L);
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void testDeactivateRecommendationSuccess() {
        // Arrange
        when(recommendationRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(recommendation));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        // Act
        ApiResponse<Boolean> response = recommendationService.deactivateRecommendation(100L, 1L);

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData());
        verify(recommendationRepository).findByIdAndUserId(1L, 100L);
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void testConcurrentRecommendationCreation() throws InterruptedException {
        // Arrange
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<ApiResponse<RecommendationDto>>> futures = new ArrayList<>();

        when(modelMapper.map(any(RecommendationDto.class), eq(Recommendation.class))).thenReturn(recommendation);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(modelMapper.map(any(Recommendation.class), eq(RecommendationDto.class))).thenReturn(recommendationDto);

        // Act
        IntStream.range(0, threadCount).forEach(i -> {
            Future<ApiResponse<RecommendationDto>> future = executorService.submit(() -> {
                try {
                    latch.countDown();
                    latch.await(); // Wait for all threads to start
                    RecommendationDto dto = new RecommendationDto();
                    dto.setUserId(100L);
                    dto.setType("WORKOUT");
                    dto.setTitle("Concurrent Test " + i);
                    return recommendationService.createRecommendation(100L, dto);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new ApiResponse<>(false, "Interrupted", null);
                }
            });
            futures.add(future);
        });

        // Assert
        List<ApiResponse<RecommendationDto>> results = new ArrayList<>();
        for (Future<ApiResponse<RecommendationDto>> future : futures) {
            try {
                results.add(future.get(5, TimeUnit.SECONDS));
            } catch (ExecutionException | TimeoutException e) {
                fail("Thread execution failed: " + e.getMessage());
            }
        }

        // All should succeed
        long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        assertEquals(threadCount, successCount, "All concurrent creations should succeed");

        executorService.shutdown();
    }

    @Test
    void testConcurrentRecommendationUpdates() throws InterruptedException {
        // Arrange
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<ApiResponse<RecommendationDto>>> futures = new ArrayList<>();

        when(recommendationRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(recommendation));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(modelMapper.map(any(Recommendation.class), eq(RecommendationDto.class))).thenReturn(recommendationDto);

        // Act
        IntStream.range(0, threadCount).forEach(i -> {
            Future<ApiResponse<RecommendationDto>> future = executorService.submit(() -> {
                try {
                    latch.countDown();
                    latch.await();
                    RecommendationDto updateDto = new RecommendationDto();
                    updateDto.setTitle("Updated Title " + i);
                    updateDto.setConfidenceScore(0.8 + i * 0.01);
                    return recommendationService.updateRecommendation(100L, 1L, updateDto);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new ApiResponse<>(false, "Interrupted", null);
                }
            });
            futures.add(future);
        });

        // Assert
        List<ApiResponse<RecommendationDto>> results = new ArrayList<>();
        for (Future<ApiResponse<RecommendationDto>> future : futures) {
            try {
                results.add(future.get(5, TimeUnit.SECONDS));
            } catch (ExecutionException | TimeoutException e) {
                fail("Thread execution failed: " + e.getMessage());
            }
        }

        // All updates should succeed
        long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        assertEquals(threadCount, successCount, "All concurrent updates should succeed");

        executorService.shutdown();
    }

    @Test
    void testConcurrentReadOperations() throws InterruptedException {
        // Arrange
        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<ApiResponse<List<RecommendationDto>>>> futures = new ArrayList<>();

        List<Recommendation> recommendations = List.of(recommendation);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        when(recommendationRepository.findByUserIdAndIsActiveOrderByPriorityAscCreatedAtDesc(
                eq(100L), eq(true), any(Pageable.class))).thenReturn(recommendationPage);
        when(modelMapper.map(any(Recommendation.class), eq(RecommendationDto.class))).thenReturn(recommendationDto);

        // Act
        IntStream.range(0, threadCount).forEach(i -> {
            Future<ApiResponse<List<RecommendationDto>>> future = executorService.submit(() -> {
                try {
                    latch.countDown();
                    latch.await();
                    return recommendationService.getRecommendationsForUser(100L, 0, 10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new ApiResponse<>(false, "Interrupted", null);
                }
            });
            futures.add(future);
        });

        // Assert
        List<ApiResponse<List<RecommendationDto>>> results = new ArrayList<>();
        for (Future<ApiResponse<List<RecommendationDto>>> future : futures) {
            try {
                results.add(future.get(5, TimeUnit.SECONDS));
            } catch (ExecutionException | TimeoutException e) {
                fail("Thread execution failed: " + e.getMessage());
            }
        }

        // All reads should succeed
        long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        assertEquals(threadCount, successCount, "All concurrent reads should succeed");

        executorService.shutdown();
    }

    @Test
    void testDatabaseConstraintViolation() {
        // Arrange
        when(modelMapper.map(recommendationDto, Recommendation.class)).thenReturn(recommendation);
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenThrow(new RuntimeException("Database constraint violation"));

        // Act
        ApiResponse<RecommendationDto> response = recommendationService.createRecommendation(100L, recommendationDto);

        // Assert
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Error creating recommendation"));
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void testTransactionRollback() {
        // Arrange
        when(recommendationRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(recommendation));
        doThrow(new RuntimeException("Database error")).when(recommendationRepository).delete(any(Recommendation.class));

        // Act & Assert
        ApiResponse<Boolean> response = recommendationService.deleteRecommendation(100L, 1L);

        // Assert
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Error deleting recommendation"));
    }

    @Test
    void testPaginatedResults() {
        // Arrange
        List<Recommendation> recommendations = IntStream.range(0, 5)
                .mapToObj(i -> {
                    Recommendation rec = new Recommendation();
                    rec.setId((long) i);
                    rec.setUserId(100L);
                    rec.setType("WORKOUT");
                    rec.setTitle("Recommendation " + i);
                    rec.setPriority(i);
                    rec.setIsActive(true);
                    return rec;
                })
                .toList();

        Page<Recommendation> recommendationPage = new PageImpl<>(
                recommendations.subList(0, 3), 
                org.springframework.data.domain.PageRequest.of(0, 3), 
                recommendations.size());

        when(recommendationRepository.findByUserIdAndIsActiveOrderByPriorityAscCreatedAtDesc(
                eq(100L), eq(true), any(Pageable.class))).thenReturn(recommendationPage);
        when(modelMapper.map(any(Recommendation.class), eq(RecommendationDto.class)))
                .thenAnswer(invocation -> {
                    Recommendation rec = invocation.getArgument(0);
                    RecommendationDto dto = new RecommendationDto();
                    dto.setId(rec.getId());
                    dto.setUserId(rec.getUserId());
                    dto.setType(rec.getType());
                    dto.setTitle(rec.getTitle());
                    dto.setPriority(rec.getPriority());
                    dto.setIsActive(rec.getIsActive());
                    return dto;
                });

        // Act
        ApiResponse<List<RecommendationDto>> response = recommendationService.getRecommendationsForUser(100L, 0, 3);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(3, response.getData().size());
        verify(recommendationRepository).findByUserIdAndIsActiveOrderByPriorityAscCreatedAtDesc(
                eq(100L), eq(true), any(Pageable.class));
    }

    @Test
    void testHighVolumeRecommendationRetrieval() throws InterruptedException {
        // Arrange
        int recommendationCount = 1000;
        List<Recommendation> recommendations = IntStream.range(0, recommendationCount)
                .mapToObj(i -> {
                    Recommendation rec = new Recommendation();
                    rec.setId((long) i);
                    rec.setUserId(100L);
                    rec.setType(i % 2 == 0 ? "WORKOUT" : "NUTRITION");
                    rec.setTitle("Recommendation " + i);
                    rec.setPriority(i % 5);
                    rec.setIsActive(true);
                    rec.setConfidenceScore(0.5 + (i % 50) * 0.01);
                    return rec;
                })
                .toList();

        // Test paginated retrieval
        int pageSize = 50;
        Page<Recommendation> firstPage = new PageImpl<>(
                recommendations.subList(0, pageSize), 
                org.springframework.data.domain.PageRequest.of(0, pageSize), 
                recommendationCount);

        when(recommendationRepository.findByUserIdAndIsActiveOrderByPriorityAscCreatedAtDesc(
                eq(100L), eq(true), any(Pageable.class))).thenReturn(firstPage);
        when(modelMapper.map(any(Recommendation.class), eq(RecommendationDto.class)))
                .thenAnswer(invocation -> {
                    Recommendation rec = invocation.getArgument(0);
                    RecommendationDto dto = new RecommendationDto();
                    dto.setId(rec.getId());
                    dto.setUserId(rec.getUserId());
                    dto.setType(rec.getType());
                    dto.setTitle(rec.getTitle());
                    return dto;
                });

        // Act
        long startTime = System.currentTimeMillis();
        ApiResponse<List<RecommendationDto>> response = recommendationService.getRecommendationsForUser(100L, 0, pageSize);
        long endTime = System.currentTimeMillis();

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(pageSize, response.getData().size());
        assertTrue((endTime - startTime) < 1000, "Should complete within 1 second"); // Performance check
    }
}
