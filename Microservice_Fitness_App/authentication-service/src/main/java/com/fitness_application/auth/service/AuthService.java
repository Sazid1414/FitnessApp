package com.fitness_application.auth.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fitness_application.auth.dto.ApiResponse;
import com.fitness_application.auth.dto.JwtResponse;
import com.fitness_application.auth.dto.LoginDto;
import com.fitness_application.auth.dto.UserDto;
import com.fitness_application.auth.dto.UserRegistrationDto;
import com.fitness_application.auth.model.User;
import com.fitness_application.auth.model.User.UserRole;
import com.fitness_application.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    
    public ApiResponse<JwtResponse> register(UserRegistrationDto registrationDto) {
        try {
            // Check if user already exists
            if (userRepository.existsByEmail(registrationDto.getEmail())) {
                return ApiResponse.error("User already exists with email: " + registrationDto.getEmail());
            }
            
            // Create new user
            User user = new User();
            user.setEmail(registrationDto.getEmail());
            user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
            user.setFirstName(registrationDto.getFirstName());
            user.setLastName(registrationDto.getLastName());
            user.setDateOfBirth(registrationDto.getDateOfBirth());
            user.setGender(registrationDto.getGender());
            user.setHeight(registrationDto.getHeight());
            user.setWeight(registrationDto.getWeight());
            user.setActivityLevel(registrationDto.getActivityLevel());
            user.setFitnessGoal(registrationDto.getFitnessGoal());
            user.setProfilePictureUrl(registrationDto.getProfilePictureUrl());
            user.setRole(UserRole.USER);
            
            User savedUser = userRepository.save(user);
            
            // Generate JWT token
            String token = jwtService.generateToken(savedUser.getEmail());
            
            JwtResponse jwtResponse = new JwtResponse(token, savedUser.getEmail(), 
                    savedUser.getFirstName(), savedUser.getLastName());
            
            return ApiResponse.success("User registered successfully", jwtResponse);
            
        } catch (Exception e) {
            return ApiResponse.error("Registration failed: " + e.getMessage());
        }
    }
    
    public ApiResponse<JwtResponse> login(LoginDto loginDto) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
            
            // Get user details
            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate JWT token
            String token = jwtService.generateToken(user.getEmail());
            
            JwtResponse jwtResponse = new JwtResponse(token, user.getEmail(), 
                    user.getFirstName(), user.getLastName());
            
            return ApiResponse.success("Login successful", jwtResponse);
            
        } catch (Exception e) {
            return ApiResponse.error("Login failed: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserDto> getCurrentUser(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return ApiResponse.success(userDto);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to get user: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserDto> updateProfile(String email, UserRegistrationDto updateDto) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Update user details
            user.setFirstName(updateDto.getFirstName());
            user.setLastName(updateDto.getLastName());
            user.setDateOfBirth(updateDto.getDateOfBirth());
            user.setGender(updateDto.getGender());
            user.setHeight(updateDto.getHeight());
            user.setWeight(updateDto.getWeight());
            user.setActivityLevel(updateDto.getActivityLevel());
            user.setFitnessGoal(updateDto.getFitnessGoal());
            user.setProfilePictureUrl(updateDto.getProfilePictureUrl());
            
            User savedUser = userRepository.save(user);
            UserDto userDto = modelMapper.map(savedUser, UserDto.class);
            
            return ApiResponse.success("Profile updated successfully", userDto);
            
        } catch (Exception e) {
            return ApiResponse.error("Profile update failed: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserDto> createAdminUser(UserRegistrationDto registrationDto) {
        try {
            if (userRepository.existsByEmail(registrationDto.getEmail())) {
                return ApiResponse.error("User already exists with email: " + registrationDto.getEmail());
            }
            
            User user = new User();
            user.setEmail(registrationDto.getEmail());
            user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
            user.setFirstName(registrationDto.getFirstName());
            user.setLastName(registrationDto.getLastName());
            user.setRole(UserRole.ADMIN);
            
            User savedUser = userRepository.save(user);
            UserDto userDto = modelMapper.map(savedUser, UserDto.class);
            
            return ApiResponse.success("Admin user created successfully", userDto);
            
        } catch (Exception e) {
            return ApiResponse.error("Admin creation failed: " + e.getMessage());
        }
    }
    
    public ApiResponse<Boolean> validateToken(String token) {
        try {
            boolean isValid = jwtService.validateToken(token);
            return ApiResponse.success(isValid);
        } catch (Exception e) {
            return ApiResponse.error("Token validation failed: " + e.getMessage());
        }
    }
}
