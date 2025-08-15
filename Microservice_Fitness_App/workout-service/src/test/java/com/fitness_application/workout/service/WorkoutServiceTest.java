package com.fitness_application.workout.service;

import com.fitness_application.workout.dto.ApiResponse;
import com.fitness_application.workout.dto.WorkoutDto;
import com.fitness_application.workout.dto.ExerciseDto;
import com.fitness_application.workout.model.Workout;
import com.fitness_application.workout.model.Exercise;
import com.fitness_application.workout.repository.WorkoutRepository;
import com.fitness_application.workout.repository.ExerciseRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WorkoutService workoutService;

    private WorkoutDto workoutDto;
    private Workout workout;
    private ExerciseDto exerciseDto;
    private Exercise exercise;
    private final Long userId = 1L;
    private final Long workoutId = 1L;

    @BeforeEach
    void setUp() {
        // Setup WorkoutDto
        workoutDto = new WorkoutDto();
        workoutDto.setId(workoutId);
        workoutDto.setUserId(userId);
        workoutDto.setName("Test Workout");
        workoutDto.setType(Workout.WorkoutType.STRENGTH);
        workoutDto.setDescription("Test workout description");
        workoutDto.setDurationMinutes(60);
        workoutDto.setCaloriesBurned(300);
        workoutDto.setWorkoutDate(LocalDateTime.now());
        workoutDto.setNotes("Test notes");

        // Setup ExerciseDto
        exerciseDto = new ExerciseDto();
        exerciseDto.setId(1L);
        exerciseDto.setName("Push ups");
        exerciseDto.setCategory(Exercise.ExerciseCategory.STRENGTH);
        exerciseDto.setSets(3);
        exerciseDto.setReps(10);
        exerciseDto.setWeight(0.0);
        exerciseDto.setNotes("Test exercise notes");

        workoutDto.setExercises(Arrays.asList(exerciseDto));

        // Setup Workout entity
        workout = new Workout();
        workout.setId(workoutId);
        workout.setUserId(userId);
        workout.setName("Test Workout");
        workout.setType(Workout.WorkoutType.STRENGTH);
        workout.setDescription("Test workout description");
        workout.setDurationMinutes(60);
        workout.setCaloriesBurned(300);
        workout.setWorkoutDate(LocalDateTime.now());
        workout.setCompleted(false);
        workout.setNotes("Test notes");

        // Setup Exercise entity
        exercise = new Exercise();
        exercise.setId(1L);
        exercise.setName("Push ups");
        exercise.setCategory(Exercise.ExerciseCategory.STRENGTH);
        exercise.setSets(3);
        exercise.setReps(10);
        exercise.setWeight(0.0);
        exercise.setNotes("Test exercise notes");
        exercise.setWorkout(workout);

        workout.setExercises(Arrays.asList(exercise));
    }

    @Test
    void createWorkout_Success() {
        // Given
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(exerciseRepository.saveAll(anyList())).thenReturn(Arrays.asList(exercise));
        when(modelMapper.map(workout, WorkoutDto.class)).thenReturn(workoutDto);

        // When
        ApiResponse<WorkoutDto> response = workoutService.createWorkout(userId, workoutDto);

        // Then
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("Workout created successfully", response.getMessage());
        verify(workoutRepository).save(any(Workout.class));
        verify(exerciseRepository).saveAll(anyList());
    }

    @Test
    void createWorkout_Exception() {
        // Given
        when(workoutRepository.save(any(Workout.class))).thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<WorkoutDto> response = workoutService.createWorkout(userId, workoutDto);

        // Then
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to create workout"));
    }

    @Test
    void getUserWorkouts_Success() {
        // Given
        List<Workout> workouts = Arrays.asList(workout);
        Page<Workout> workoutPage = new PageImpl<>(workouts);
        
        when(workoutRepository.findByUserIdOrderByWorkoutDateDesc(eq(userId), any(Pageable.class)))
                .thenReturn(workoutPage);
        when(exerciseRepository.findByWorkoutIdOrderById(workoutId)).thenReturn(Arrays.asList(exercise));
        when(modelMapper.map(workout, WorkoutDto.class)).thenReturn(workoutDto);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(exerciseDto);

        // When
        ApiResponse<List<WorkoutDto>> response = workoutService.getUserWorkouts(userId, 0, 10);

        // Then
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(workoutRepository).findByUserIdOrderByWorkoutDateDesc(eq(userId), any(Pageable.class));
    }

    @Test
    void getWorkoutById_Success() {
        // Given
        when(workoutRepository.findByIdAndUserId(workoutId, userId)).thenReturn(Optional.of(workout));
        when(exerciseRepository.findByWorkoutIdOrderById(workoutId)).thenReturn(Arrays.asList(exercise));
        when(modelMapper.map(workout, WorkoutDto.class)).thenReturn(workoutDto);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(exerciseDto);

        // When
        ApiResponse<WorkoutDto> response = workoutService.getWorkoutById(userId, workoutId);

        // Then
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(workoutId, response.getData().getId());
    }

    @Test
    void getWorkoutById_NotFound() {
        // Given
        when(workoutRepository.findByIdAndUserId(workoutId, userId)).thenReturn(Optional.empty());

        // When
        ApiResponse<WorkoutDto> response = workoutService.getWorkoutById(userId, workoutId);

        // Then
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to fetch workout"));
    }

    @Test
    void updateWorkout_Success() {
        // Given
        when(workoutRepository.findByIdAndUserId(workoutId, userId)).thenReturn(Optional.of(workout));
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(exerciseRepository.saveAll(anyList())).thenReturn(Arrays.asList(exercise));
        when(modelMapper.map(workout, WorkoutDto.class)).thenReturn(workoutDto);

        // When
        ApiResponse<WorkoutDto> response = workoutService.updateWorkout(userId, workoutId, workoutDto);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Workout updated successfully", response.getMessage());
        verify(exerciseRepository).deleteByWorkoutId(workoutId);
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    void deleteWorkout_Success() {
        // Given
        when(workoutRepository.findByIdAndUserId(workoutId, userId)).thenReturn(Optional.of(workout));

        // When
        ApiResponse<Void> response = workoutService.deleteWorkout(userId, workoutId);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Workout deleted successfully", response.getMessage());
        verify(exerciseRepository).deleteByWorkoutId(workoutId);
        verify(workoutRepository).delete(workout);
    }

    @Test
    void completeWorkout_Success() {
        // Given
        when(workoutRepository.findByIdAndUserId(workoutId, userId)).thenReturn(Optional.of(workout));
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(modelMapper.map(workout, WorkoutDto.class)).thenReturn(workoutDto);

        // When
        ApiResponse<WorkoutDto> response = workoutService.completeWorkout(userId, workoutId);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Workout completed successfully", response.getMessage());
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    void getCompletedWorkouts_Success() {
        // Given
        when(workoutRepository.findByUserIdAndCompleted(userId, true)).thenReturn(Arrays.asList(workout));
        when(modelMapper.map(workout, WorkoutDto.class)).thenReturn(workoutDto);

        // When
        ApiResponse<List<WorkoutDto>> response = workoutService.getCompletedWorkouts(userId);

        // Then
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
    }

    @Test
    void getWorkoutCount_Success() {
        // Given
        when(workoutRepository.countWorkoutsByUser(userId)).thenReturn(5L);

        // When
        ApiResponse<Long> response = workoutService.getWorkoutCount(userId);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(5L, response.getData());
    }

    // Concurrency Tests
    @Test
    void createWorkout_ConcurrentAccess() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(10);
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(exerciseRepository.saveAll(anyList())).thenReturn(Arrays.asList(exercise));
        when(modelMapper.map(workout, WorkoutDto.class)).thenReturn(workoutDto);

        // When
        CompletableFuture<ApiResponse<WorkoutDto>>[] futures = new CompletableFuture[5];
        for (int i = 0; i < 5; i++) {
            final int index = i;
            futures[i] = CompletableFuture.supplyAsync(() -> {
                WorkoutDto dto = new WorkoutDto();
                dto.setName("Concurrent Workout " + index);
                dto.setUserId(userId);
                dto.setType(Workout.WorkoutType.CARDIO);
                dto.setDurationMinutes(30);
                return workoutService.createWorkout(userId, dto);
            }, executor);
        }

        // Then
        CompletableFuture.allOf(futures).join();
        
        for (CompletableFuture<ApiResponse<WorkoutDto>> future : futures) {
            ApiResponse<WorkoutDto> response = future.get();
            assertTrue(response.isSuccess());
        }

        // Verify that save was called for each concurrent request
        verify(workoutRepository, times(5)).save(any(Workout.class));
        
        executor.shutdown();
    }

    @Test
    void updateWorkout_ConcurrentAccess() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(5);
        when(workoutRepository.findByIdAndUserId(workoutId, userId)).thenReturn(Optional.of(workout));
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(modelMapper.map(workout, WorkoutDto.class)).thenReturn(workoutDto);

        // When
        CompletableFuture<ApiResponse<WorkoutDto>>[] futures = new CompletableFuture[3];
        for (int i = 0; i < 3; i++) {
            final int index = i;
            futures[i] = CompletableFuture.supplyAsync(() -> {
                WorkoutDto dto = new WorkoutDto();
                dto.setName("Updated Workout " + index);
                dto.setType(Workout.WorkoutType.FLEXIBILITY);
                dto.setDurationMinutes(45);
                return workoutService.updateWorkout(userId, workoutId, dto);
            }, executor);
        }

        // Then
        CompletableFuture.allOf(futures).join();
        
        for (CompletableFuture<ApiResponse<WorkoutDto>> future : futures) {
            ApiResponse<WorkoutDto> response = future.get();
            assertTrue(response.isSuccess());
        }

        // Verify concurrent updates
        verify(workoutRepository, times(3)).save(any(Workout.class));
        
        executor.shutdown();
    }

    @Test 
    void databaseConcurrency_MultipleUsersCreatingWorkouts() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(10);
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(modelMapper.map(workout, WorkoutDto.class)).thenReturn(workoutDto);

        // When - Simulate multiple users creating workouts simultaneously
        CompletableFuture<ApiResponse<WorkoutDto>>[] futures = new CompletableFuture[10];
        for (int i = 0; i < 10; i++) {
            final Long currentUserId = (long) (i + 1);
            futures[i] = CompletableFuture.supplyAsync(() -> {
                WorkoutDto dto = new WorkoutDto();
                dto.setName("User " + currentUserId + " Workout");
                dto.setUserId(currentUserId);
                dto.setType(Workout.WorkoutType.STRENGTH);
                dto.setDurationMinutes(60);
                return workoutService.createWorkout(currentUserId, dto);
            }, executor);
        }

        // Then
        CompletableFuture.allOf(futures).join();
        
        // Verify all requests succeeded
        for (CompletableFuture<ApiResponse<WorkoutDto>> future : futures) {
            ApiResponse<WorkoutDto> response = future.get();
            assertTrue(response.isSuccess());
        }

        verify(workoutRepository, times(10)).save(any(Workout.class));
        
        executor.shutdown();
    }

    @Test
    void transactionRollback_WhenExceptionOccurs() {
        // Given
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(exerciseRepository.saveAll(anyList())).thenThrow(new RuntimeException("Database constraint violation"));

        // When
        ApiResponse<WorkoutDto> response = workoutService.createWorkout(userId, workoutDto);

        // Then
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to create workout"));
        
        // Verify workout was attempted to be saved but transaction should rollback
        verify(workoutRepository).save(any(Workout.class));
        verify(exerciseRepository).saveAll(anyList());
    }
}
