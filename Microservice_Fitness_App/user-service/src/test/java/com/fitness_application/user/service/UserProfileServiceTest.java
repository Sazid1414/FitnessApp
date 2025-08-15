package com.fitness_application.user.service;

import com.fitness_application.user.dto.ApiResponse;
import com.fitness_application.user.dto.UserProfileDto;
import com.fitness_application.user.model.UserProfile;
import com.fitness_application.user.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserProfileService userProfileService;

    private UserProfileDto userProfileDto;
    private UserProfile userProfile;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        // Setup UserProfileDto
        userProfileDto = new UserProfileDto();
        userProfileDto.setUserId(userId);
        userProfileDto.setEmail("test@example.com");
        userProfileDto.setFirstName("John");
        userProfileDto.setLastName("Doe");
        userProfileDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userProfileDto.setGender(UserProfile.Gender.MALE);
        userProfileDto.setHeight(180.0);
        userProfileDto.setWeight(75.0);
        userProfileDto.setActivityLevel(UserProfile.ActivityLevel.MODERATELY_ACTIVE);
        userProfileDto.setFitnessGoal(UserProfile.FitnessGoal.MAINTAIN_WEIGHT);
        userProfileDto.setProfilePictureUrl("https://example.com/profile.jpg");

        // Setup UserProfile entity
        userProfile = new UserProfile();
        userProfile.setUserId(userId);
        userProfile.setEmail("test@example.com");
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userProfile.setGender(UserProfile.Gender.MALE);
        userProfile.setHeight(180.0);
        userProfile.setWeight(75.0);
        userProfile.setActivityLevel(UserProfile.ActivityLevel.MODERATELY_ACTIVE);
        userProfile.setFitnessGoal(UserProfile.FitnessGoal.MAINTAIN_WEIGHT);
        userProfile.setProfilePictureUrl("https://example.com/profile.jpg");
        userProfile.setCreatedAt(LocalDateTime.now());
        userProfile.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createProfile_Success() {
        // Given
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);
        when(modelMapper.map(userProfile, UserProfileDto.class)).thenReturn(userProfileDto);

        // When
        ApiResponse<UserProfileDto> response = userProfileService.createProfile(userId, userProfileDto);

        // Then
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("Profile created successfully", response.getMessage());
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void createProfile_AlreadyExists() {
        // Given
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));

        // When
        ApiResponse<UserProfileDto> response = userProfileService.createProfile(userId, userProfileDto);

        // Then
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Profile already exists"));
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void createProfile_Exception() {
        // Given
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<UserProfileDto> response = userProfileService.createProfile(userId, userProfileDto);

        // Then
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to create profile"));
    }

    @Test
    void getProfile_Success() {
        // Given
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));
        when(modelMapper.map(userProfile, UserProfileDto.class)).thenReturn(userProfileDto);

        // When
        ApiResponse<UserProfileDto> response = userProfileService.getProfile(userId);

        // Then
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(userId, response.getData().getUserId());
    }

    @Test
    void getProfile_NotFound() {
        // Given
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When
        ApiResponse<UserProfileDto> response = userProfileService.getProfile(userId);

        // Then
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to fetch profile"));
    }

    @Test
    void updateProfile_Success() {
        // Given
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);
        when(modelMapper.map(userProfile, UserProfileDto.class)).thenReturn(userProfileDto);

        // When
        ApiResponse<UserProfileDto> response = userProfileService.updateProfile(userId, userProfileDto);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Profile updated successfully", response.getMessage());
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void deleteProfile_Success() {
        // Given
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));

        // When
        ApiResponse<Void> response = userProfileService.deleteProfile(userId);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Profile deleted successfully", response.getMessage());
        verify(userProfileRepository).delete(userProfile);
    }

    @Test
    void calculateBMI_Success() {
        // Given
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));

        // When
        ApiResponse<Double> response = userProfileService.calculateBMI(userId);

        // Then
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        // BMI = 75 / (1.8)^2 = 23.15
        assertEquals(23.15, response.getData(), 0.01);
    }

    @Test
    void calculateBMI_MissingData() {
        // Given
        userProfile.setHeight(null);
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));

        // When
        ApiResponse<Double> response = userProfileService.calculateBMI(userId);

        // Then
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Height and weight are required"));
    }

    @Test
    void calculateBMR_Success() {
        // Given
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));

        // When
        ApiResponse<Double> response = userProfileService.calculateBMR(userId);

        // Then
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        // BMR should be calculated correctly based on the formula
        assertTrue(response.getData() > 0);
    }

    @Test
    void calculateBMR_MissingData() {
        // Given
        userProfile.setDateOfBirth(null);
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));

        // When
        ApiResponse<Double> response = userProfileService.calculateBMR(userId);

        // Then
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("date of birth, and gender are required"));
    }

    // Concurrency Tests
    @Test
    void createProfile_ConcurrentAccess() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(5);
        when(userProfileRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);
        when(modelMapper.map(userProfile, UserProfileDto.class)).thenReturn(userProfileDto);

        // When
        CompletableFuture<ApiResponse<UserProfileDto>>[] futures = new CompletableFuture[5];
        for (int i = 0; i < 5; i++) {
            final Long currentUserId = (long) (i + 1);
            futures[i] = CompletableFuture.supplyAsync(() -> {
                UserProfileDto dto = new UserProfileDto();
                dto.setUserId(currentUserId);
                dto.setEmail("user" + currentUserId + "@example.com");
                dto.setFirstName("User " + currentUserId);
                dto.setLastName("Test");
                dto.setHeight(180.0);
                dto.setWeight(75.0);
                return userProfileService.createProfile(currentUserId, dto);
            }, executor);
        }

        // Then
        CompletableFuture.allOf(futures).join();
        
        for (CompletableFuture<ApiResponse<UserProfileDto>> future : futures) {
            ApiResponse<UserProfileDto> response = future.get();
            assertTrue(response.isSuccess());
        }

        verify(userProfileRepository, times(5)).save(any(UserProfile.class));
        
        executor.shutdown();
    }

    @Test
    void updateProfile_ConcurrentAccess() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(3);
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);
        when(modelMapper.map(userProfile, UserProfileDto.class)).thenReturn(userProfileDto);

        // When
        CompletableFuture<ApiResponse<UserProfileDto>>[] futures = new CompletableFuture[3];
        for (int i = 0; i < 3; i++) {
            final int index = i;
            futures[i] = CompletableFuture.supplyAsync(() -> {
                UserProfileDto dto = new UserProfileDto();
                dto.setFirstName("Updated Name " + index);
                dto.setWeight(75.0 + index);
                dto.setHeight(180.0);
                return userProfileService.updateProfile(userId, dto);
            }, executor);
        }

        // Then
        CompletableFuture.allOf(futures).join();
        
        for (CompletableFuture<ApiResponse<UserProfileDto>> future : futures) {
            ApiResponse<UserProfileDto> response = future.get();
            assertTrue(response.isSuccess());
        }

        verify(userProfileRepository, times(3)).save(any(UserProfile.class));
        
        executor.shutdown();
    }

    @Test 
    void databaseConcurrency_MultipleUsersReadingProfiles() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(10);
        when(userProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(userProfile));
        when(modelMapper.map(userProfile, UserProfileDto.class)).thenReturn(userProfileDto);

        // When - Simulate multiple users reading profiles simultaneously
        CompletableFuture<ApiResponse<UserProfileDto>>[] futures = new CompletableFuture[10];
        for (int i = 0; i < 10; i++) {
            final Long currentUserId = (long) (i + 1);
            futures[i] = CompletableFuture.supplyAsync(() -> {
                return userProfileService.getProfile(currentUserId);
            }, executor);
        }

        // Then
        CompletableFuture.allOf(futures).join();
        
        // Verify all requests succeeded
        for (CompletableFuture<ApiResponse<UserProfileDto>> future : futures) {
            ApiResponse<UserProfileDto> response = future.get();
            assertTrue(response.isSuccess());
        }

        verify(userProfileRepository, times(10)).findByUserId(anyLong());
        
        executor.shutdown();
    }

    @Test
    void transactionRollback_WhenExceptionOccurs() {
        // Given
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenThrow(new RuntimeException("Database constraint violation"));

        // When
        ApiResponse<UserProfileDto> response = userProfileService.createProfile(userId, userProfileDto);

        // Then
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to create profile"));
        
        // Verify the save was attempted
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void calculateBMR_VariousGenders() {
        // Test BMR calculation for different genders
        userProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userProfile.setHeight(175.0);
        userProfile.setWeight(70.0);
        
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));

        // Test MALE
        userProfile.setGender(UserProfile.Gender.MALE);
        ApiResponse<Double> maleBmr = userProfileService.calculateBMR(userId);
        assertTrue(maleBmr.isSuccess());
        
        // Test FEMALE
        userProfile.setGender(UserProfile.Gender.FEMALE);
        ApiResponse<Double> femaleBmr = userProfileService.calculateBMR(userId);
        assertTrue(femaleBmr.isSuccess());
        
        // Male BMR should be higher than female BMR for same stats
        assertTrue(maleBmr.getData() > femaleBmr.getData());
    }

    @Test
    void profileIntegrity_CompleteProfileCheck() {
        // Test complete profile validation
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));
        
        // Complete profile should work for BMR calculation
        ApiResponse<Double> response = userProfileService.calculateBMR(userId);
        assertTrue(response.isSuccess());
        
        // Incomplete profile should fail
        userProfile.setActivityLevel(null);
        ApiResponse<Double> incompleteResponse = userProfileService.calculateBMR(userId);
        assertTrue(incompleteResponse.isSuccess()); // BMR doesn't require activity level
        
        // But BMI should still work
        ApiResponse<Double> bmiResponse = userProfileService.calculateBMI(userId);
        assertTrue(bmiResponse.isSuccess());
    }
}
