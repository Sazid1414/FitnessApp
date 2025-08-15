package com.fitness_application.user.service;

import com.fitness_application.user.dto.ApiResponse;
import com.fitness_application.user.dto.UserProfileDto;
import com.fitness_application.user.model.UserProfile;
import com.fitness_application.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;
    
    public ApiResponse<UserProfileDto> createProfile(Long userId, UserProfileDto profileDto) {
        try {
            log.info("Creating profile for user: {}", userId);
            
            // Check if profile already exists
            if (userProfileRepository.findByUserId(userId).isPresent()) {
                return ApiResponse.error("Profile already exists for user: " + userId);
            }
            
            UserProfile profile = new UserProfile();
            profile.setUserId(userId);
            profile.setFirstName(profileDto.getFirstName());
            profile.setLastName(profileDto.getLastName());
            profile.setDateOfBirth(profileDto.getDateOfBirth());
            profile.setGender(profileDto.getGender());
            profile.setHeight(profileDto.getHeight());
            profile.setWeight(profileDto.getWeight());
            profile.setActivityLevel(profileDto.getActivityLevel());
            profile.setFitnessGoal(profileDto.getFitnessGoal());
            profile.setProfilePictureUrl(profileDto.getProfilePictureUrl());
            profile.setEmail(profileDto.getEmail());
            
            UserProfile savedProfile = userProfileRepository.save(profile);
            UserProfileDto responseDto = modelMapper.map(savedProfile, UserProfileDto.class);
            
            return ApiResponse.success("Profile created successfully", responseDto);
            
        } catch (Exception e) {
            log.error("Error creating profile for user {}: {}", userId, e.getMessage(), e);
            return ApiResponse.error("Failed to create profile: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserProfileDto> getProfile(Long userId) {
        try {
            log.info("Fetching profile for user: {}", userId);
            
            UserProfile profile = userProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
            
            UserProfileDto profileDto = modelMapper.map(profile, UserProfileDto.class);
            return ApiResponse.success("Profile retrieved successfully", profileDto);
            
        } catch (Exception e) {
            log.error("Error fetching profile for user {}: {}", userId, e.getMessage(), e);
            return ApiResponse.error("Failed to fetch profile: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserProfileDto> updateProfile(Long userId, UserProfileDto profileDto) {
        try {
            log.info("Updating profile for user: {}", userId);
            
            UserProfile profile = userProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
            
            // Update fields
            profile.setFirstName(profileDto.getFirstName());
            profile.setLastName(profileDto.getLastName());
            profile.setDateOfBirth(profileDto.getDateOfBirth());
            profile.setGender(profileDto.getGender());
            profile.setHeight(profileDto.getHeight());
            profile.setWeight(profileDto.getWeight());
            profile.setActivityLevel(profileDto.getActivityLevel());
            profile.setFitnessGoal(profileDto.getFitnessGoal());
            profile.setProfilePictureUrl(profileDto.getProfilePictureUrl());
            
            UserProfile savedProfile = userProfileRepository.save(profile);
            UserProfileDto responseDto = modelMapper.map(savedProfile, UserProfileDto.class);
            
            return ApiResponse.success("Profile updated successfully", responseDto);
            
        } catch (Exception e) {
            log.error("Error updating profile for user {}: {}", userId, e.getMessage(), e);
            return ApiResponse.error("Failed to update profile: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserProfileDto> getProfileById(Long profileId) {
        try {
            log.info("Fetching profile by ID: {}", profileId);
            
            UserProfile profile = userProfileRepository.findById(profileId)
                    .orElseThrow(() -> new RuntimeException("Profile not found with ID: " + profileId));
            
            UserProfileDto profileDto = modelMapper.map(profile, UserProfileDto.class);
            return ApiResponse.success("Profile retrieved successfully", profileDto);
            
        } catch (Exception e) {
            log.error("Error fetching profile with ID {}: {}", profileId, e.getMessage(), e);
            return ApiResponse.error("Failed to fetch profile: " + e.getMessage());
        }
    }
    
    public ApiResponse<Void> deleteProfile(Long userId) {
        try {
            log.info("Deleting profile for user: {}", userId);
            
            UserProfile profile = userProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
            
            userProfileRepository.delete(profile);
            
            return ApiResponse.success("Profile deleted successfully", null);
            
        } catch (Exception e) {
            log.error("Error deleting profile for user {}: {}", userId, e.getMessage(), e);
            return ApiResponse.error("Failed to delete profile: " + e.getMessage());
        }
    }
    
    public ApiResponse<Double> calculateBMI(Long userId) {
        try {
            UserProfile profile = userProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
            
            if (profile.getHeight() == null || profile.getWeight() == null) {
                return ApiResponse.error("Height and weight are required for BMI calculation");
            }
            
            // BMI = weight (kg) / (height (m))^2
            double heightInMeters = profile.getHeight() / 100.0;
            double bmi = profile.getWeight() / (heightInMeters * heightInMeters);
            return ApiResponse.success("BMI calculated successfully", bmi);
            
        } catch (Exception e) {
            log.error("Error calculating BMI for user {}: {}", userId, e.getMessage(), e);
            return ApiResponse.error("Failed to calculate BMI: " + e.getMessage());
        }
    }
    
    public ApiResponse<Double> calculateBMR(Long userId) {
        try {
            UserProfile profile = userProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
            
            if (profile.getHeight() == null || profile.getWeight() == null || 
                profile.getDateOfBirth() == null || profile.getGender() == null) {
                return ApiResponse.error("Height, weight, date of birth, and gender are required for BMR calculation");
            }
            
            double bmr = profile.calculateBasalMetabolicRate();
            return ApiResponse.success("BMR calculated successfully", bmr);
            
        } catch (Exception e) {
            log.error("Error calculating BMR for user {}: {}", userId, e.getMessage(), e);
            return ApiResponse.error("Failed to calculate BMR: " + e.getMessage());
        }
    }
}
