package com.fitness_application.nutrition.service;

import com.fitness_application.nutrition.dto.ApiResponse;
import com.fitness_application.nutrition.dto.FoodEntryDto;
import com.fitness_application.nutrition.dto.NutritionLogDto;
import com.fitness_application.nutrition.model.FoodEntry;
import com.fitness_application.nutrition.model.NutritionLog;
import com.fitness_application.nutrition.repository.FoodEntryRepository;
import com.fitness_application.nutrition.repository.NutritionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NutritionServiceTest {

    @Mock
    private NutritionLogRepository nutritionLogRepository;

    @Mock
    private FoodEntryRepository foodEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NutritionService nutritionService;

    private NutritionLog nutritionLog;
    private NutritionLogDto nutritionLogDto;
    private FoodEntry foodEntry;
    private FoodEntryDto foodEntryDto;

    @BeforeEach
    void setUp() {
        nutritionLog = new NutritionLog();
        nutritionLog.setId(1L);
        nutritionLog.setUserId(100L);
        nutritionLog.setDate("2024-01-15");
        nutritionLog.setTotalCalories(2000.0);
        nutritionLog.setTotalCarbohydrates(250.0);
        nutritionLog.setTotalProtein(150.0);
        nutritionLog.setTotalFat(70.0);

        nutritionLogDto = new NutritionLogDto();
        nutritionLogDto.setId(1L);
        nutritionLogDto.setUserId(100L);
        nutritionLogDto.setDate("2024-01-15");
        nutritionLogDto.setTotalCalories(2000.0);
        nutritionLogDto.setTotalCarbohydrates(250.0);
        nutritionLogDto.setTotalProtein(150.0);
        nutritionLogDto.setTotalFat(70.0);

        foodEntry = new FoodEntry();
        foodEntry.setId(1L);
        foodEntry.setFoodName("Apple");
        foodEntry.setCalories(95);
        foodEntry.setCarbohydrates(25.0);
        foodEntry.setProtein(0.5);
        foodEntry.setFat(0.3);
        foodEntry.setServingSize(1.0);
        foodEntry.setUnit("medium");
        foodEntry.setBrandName("Generic");
        foodEntry.setNutritionLog(nutritionLog);

        foodEntryDto = new FoodEntryDto();
        foodEntryDto.setId(1L);
        foodEntryDto.setFoodName("Apple");
        foodEntryDto.setCalories(95);
        foodEntryDto.setCarbohydrates(25.0);
        foodEntryDto.setProtein(0.5);
        foodEntryDto.setFat(0.3);
        foodEntryDto.setServingSize(1.0);
        foodEntryDto.setUnit("medium");
        foodEntryDto.setBrandName("Generic");
    }

    @Test
    void testCreateNutritionLogSuccess() {
        // Arrange
        when(nutritionLogRepository.findByUserIdAndDate(100L, "2024-01-15"))
                .thenReturn(Optional.empty());
        when(modelMapper.map(nutritionLogDto, NutritionLog.class)).thenReturn(nutritionLog);
        when(nutritionLogRepository.save(any(NutritionLog.class))).thenReturn(nutritionLog);
        when(modelMapper.map(nutritionLog, NutritionLogDto.class)).thenReturn(nutritionLogDto);

        // Act
        ApiResponse<NutritionLogDto> response = nutritionService.createNutritionLog(100L, nutritionLogDto);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(nutritionLogDto, response.getData());
        verify(nutritionLogRepository).findByUserIdAndDate(100L, "2024-01-15");
        verify(nutritionLogRepository).save(any(NutritionLog.class));
    }

    @Test
    void testCreateNutritionLogAlreadyExists() {
        // Arrange
        when(nutritionLogRepository.findByUserIdAndDate(100L, "2024-01-15"))
                .thenReturn(Optional.of(nutritionLog));

        // Act
        ApiResponse<NutritionLogDto> response = nutritionService.createNutritionLog(100L, nutritionLogDto);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Nutrition log already exists for this date", response.getMessage());
        verify(nutritionLogRepository).findByUserIdAndDate(100L, "2024-01-15");
        verify(nutritionLogRepository, never()).save(any(NutritionLog.class));
    }

    @Test
    void testGetNutritionLogsSuccess() {
        // Arrange
        List<NutritionLog> logs = List.of(nutritionLog);
        Page<NutritionLog> logPage = new PageImpl<>(logs);
        when(nutritionLogRepository.findByUserId(eq(100L), any(Pageable.class))).thenReturn(logPage);
        when(modelMapper.map(nutritionLog, NutritionLogDto.class)).thenReturn(nutritionLogDto);

        // Act
        ApiResponse<List<NutritionLogDto>> response = nutritionService.getNutritionLogs(100L, 0, 10);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        assertEquals(nutritionLogDto, response.getData().get(0));
        verify(nutritionLogRepository).findByUserId(eq(100L), any(Pageable.class));
    }

    @Test
    void testGetNutritionLogByIdSuccess() {
        // Arrange
        when(nutritionLogRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(nutritionLog));
        when(modelMapper.map(nutritionLog, NutritionLogDto.class)).thenReturn(nutritionLogDto);

        // Act
        ApiResponse<NutritionLogDto> response = nutritionService.getNutritionLogById(100L, 1L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(nutritionLogDto, response.getData());
        verify(nutritionLogRepository).findByIdAndUserId(1L, 100L);
    }

    @Test
    void testGetNutritionLogByIdNotFound() {
        // Arrange
        when(nutritionLogRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<NutritionLogDto> response = nutritionService.getNutritionLogById(100L, 1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Nutrition log not found", response.getMessage());
        verify(nutritionLogRepository).findByIdAndUserId(1L, 100L);
    }

    @Test
    void testGetNutritionLogByDateSuccess() {
        // Arrange
        LocalDate date = LocalDate.parse("2024-01-15");
        when(nutritionLogRepository.findByUserIdAndDate(100L, "2024-01-15")).thenReturn(Optional.of(nutritionLog));
        when(modelMapper.map(nutritionLog, NutritionLogDto.class)).thenReturn(nutritionLogDto);

        // Act
        ApiResponse<NutritionLogDto> response = nutritionService.getNutritionLogByDate(100L, date);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(nutritionLogDto, response.getData());
        verify(nutritionLogRepository).findByUserIdAndDate(100L, "2024-01-15");
    }

    @Test
    void testUpdateNutritionLogSuccess() {
        // Arrange
        when(nutritionLogRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(nutritionLog));
        when(nutritionLogRepository.save(any(NutritionLog.class))).thenReturn(nutritionLog);
        when(modelMapper.map(nutritionLog, NutritionLogDto.class)).thenReturn(nutritionLogDto);

        nutritionLogDto.setTotalCalories(2200.0);

        // Act
        ApiResponse<NutritionLogDto> response = nutritionService.updateNutritionLog(1L, 100L, nutritionLogDto);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(nutritionLogDto, response.getData());
        verify(nutritionLogRepository).findByIdAndUserId(1L, 100L);
        verify(nutritionLogRepository).save(any(NutritionLog.class));
    }

    @Test
    void testDeleteNutritionLogSuccess() {
        // Arrange
        when(nutritionLogRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(nutritionLog));

        // Act
        nutritionService.deleteNutritionLog(1L, 100L);

        // Assert
        verify(nutritionLogRepository).findByIdAndUserId(1L, 100L);
        verify(foodEntryRepository).deleteByNutritionLogId(1L);
        verify(nutritionLogRepository).delete(nutritionLog);
    }

    @Test
    void testGetNutritionLogCountSuccess() {
        // Arrange
        when(nutritionLogRepository.countByUserId(100L)).thenReturn(5L);

        // Act
        ApiResponse<Long> response = nutritionService.getNutritionLogCount(100L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(5L, response.getData());
        verify(nutritionLogRepository).countByUserId(100L);
    }

    @Test
    void testConcurrentNutritionLogCreation() throws InterruptedException {
        // Arrange
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<ApiResponse<NutritionLogDto>>> futures = new ArrayList<>();

        // Mock repository to simulate race condition
        when(nutritionLogRepository.findByUserIdAndDate(100L, "2024-01-15"))
                .thenReturn(Optional.empty());
        when(modelMapper.map(any(NutritionLogDto.class), eq(NutritionLog.class))).thenReturn(nutritionLog);
        when(nutritionLogRepository.save(any(NutritionLog.class))).thenReturn(nutritionLog);
        when(modelMapper.map(any(NutritionLog.class), eq(NutritionLogDto.class))).thenReturn(nutritionLogDto);

        // Act
        IntStream.range(0, threadCount).forEach(i -> {
            Future<ApiResponse<NutritionLogDto>> future = executorService.submit(() -> {
                try {
                    latch.countDown();
                    latch.await(); // Wait for all threads to start
                    return nutritionService.createNutritionLog(100L, nutritionLogDto);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new ApiResponse<>(false, "Interrupted", null);
                }
            });
            futures.add(future);
        });

        // Assert
        List<ApiResponse<NutritionLogDto>> results = new ArrayList<>();
        for (Future<ApiResponse<NutritionLogDto>> future : futures) {
            try {
                results.add(future.get(5, TimeUnit.SECONDS));
            } catch (ExecutionException | TimeoutException e) {
                fail("Thread execution failed: " + e.getMessage());
            }
        }

        // At least one should succeed
        long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        assertTrue(successCount > 0, "At least one thread should succeed");

        executorService.shutdown();
    }

    @Test
    void testConcurrentNutritionLogUpdates() throws InterruptedException {
        // Arrange
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<ApiResponse<NutritionLogDto>>> futures = new ArrayList<>();

        when(nutritionLogRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(nutritionLog));
        when(nutritionLogRepository.save(any(NutritionLog.class))).thenReturn(nutritionLog);
        when(modelMapper.map(any(NutritionLog.class), eq(NutritionLogDto.class))).thenReturn(nutritionLogDto);

        // Act
        IntStream.range(0, threadCount).forEach(i -> {
            Future<ApiResponse<NutritionLogDto>> future = executorService.submit(() -> {
                try {
                    latch.countDown();
                    latch.await();
                    NutritionLogDto updateDto = new NutritionLogDto();
                    updateDto.setTotalCalories(2000.0 + i * 100);
                    return nutritionService.updateNutritionLog(1L, 100L, updateDto);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new ApiResponse<>(false, "Interrupted", null);
                }
            });
            futures.add(future);
        });

        // Assert
        List<ApiResponse<NutritionLogDto>> results = new ArrayList<>();
        for (Future<ApiResponse<NutritionLogDto>> future : futures) {
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
        List<Future<ApiResponse<List<NutritionLogDto>>>> futures = new ArrayList<>();

        List<NutritionLog> logs = List.of(nutritionLog);
        Page<NutritionLog> logPage = new PageImpl<>(logs);
        when(nutritionLogRepository.findByUserId(eq(100L), any(Pageable.class))).thenReturn(logPage);
        when(modelMapper.map(any(NutritionLog.class), eq(NutritionLogDto.class))).thenReturn(nutritionLogDto);

        // Act
        IntStream.range(0, threadCount).forEach(i -> {
            Future<ApiResponse<List<NutritionLogDto>>> future = executorService.submit(() -> {
                try {
                    latch.countDown();
                    latch.await();
                    return nutritionService.getNutritionLogs(100L, 0, 10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new ApiResponse<>(false, "Interrupted", null);
                }
            });
            futures.add(future);
        });

        // Assert
        List<ApiResponse<List<NutritionLogDto>>> results = new ArrayList<>();
        for (Future<ApiResponse<List<NutritionLogDto>>> future : futures) {
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
        when(nutritionLogRepository.findByUserIdAndDate(100L, "2024-01-15"))
                .thenReturn(Optional.empty());
        when(modelMapper.map(nutritionLogDto, NutritionLog.class)).thenReturn(nutritionLog);
        when(nutritionLogRepository.save(any(NutritionLog.class)))
                .thenThrow(new RuntimeException("Database constraint violation"));

        // Act
        ApiResponse<NutritionLogDto> response = nutritionService.createNutritionLog(100L, nutritionLogDto);

        // Assert
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Error creating nutrition log"));
        verify(nutritionLogRepository).save(any(NutritionLog.class));
    }

    @Test
    void testTransactionRollback() {
        // Arrange
        when(nutritionLogRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(nutritionLog));
        doThrow(new RuntimeException("Database error")).when(foodEntryRepository).deleteByNutritionLogId(1L);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            nutritionService.deleteNutritionLog(1L, 100L);
        });

        // The transaction should rollback, so repository delete should not be called
        verify(nutritionLogRepository, never()).delete(any(NutritionLog.class));
    }

    @Test
    void testPaginatedResults() {
        // Arrange
        List<NutritionLog> logs = IntStream.range(0, 5)
                .mapToObj(i -> {
                    NutritionLog log = new NutritionLog();
                    log.setId((long) i);
                    log.setUserId(100L);
                    log.setDate("2024-01-" + (15 + i));
                    return log;
                })
                .toList();

        Page<NutritionLog> logPage = new PageImpl<>(logs.subList(0, 3), PageRequest.of(0, 3), logs.size());
        when(nutritionLogRepository.findByUserId(eq(100L), any(Pageable.class))).thenReturn(logPage);
        when(modelMapper.map(any(NutritionLog.class), eq(NutritionLogDto.class)))
                .thenAnswer(invocation -> {
                    NutritionLog log = invocation.getArgument(0);
                    NutritionLogDto dto = new NutritionLogDto();
                    dto.setId(log.getId());
                    dto.setUserId(log.getUserId());
                    dto.setDate(log.getDate());
                    return dto;
                });

        // Act
        ApiResponse<List<NutritionLogDto>> response = nutritionService.getNutritionLogs(100L, 0, 3);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(3, response.getData().size());
        verify(nutritionLogRepository).findByUserId(eq(100L), any(Pageable.class));
    }
}
