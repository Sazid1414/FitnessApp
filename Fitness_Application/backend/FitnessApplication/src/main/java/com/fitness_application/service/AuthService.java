package com.fitness_application.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitness_application.domain.enums.UserRole;
import com.fitness_application.dto.ApiResponse;
import com.fitness_application.dto.JwtResponse;
import com.fitness_application.dto.LoginDto;
import com.fitness_application.dto.UserDto;
import com.fitness_application.dto.UserRegistrationDto;
import com.fitness_application.exception.BadRequestException;
import com.fitness_application.exception.ResourceNotFoundException;
import com.fitness_application.model.User;
import com.fitness_application.repository.UserRepository;
import com.fitness_application.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    
    public ApiResponse<JwtResponse> register(UserRegistrationDto registrationDto) {
        try {
            if (registrationDto.getEmail() == null || registrationDto.getPassword() == null) {
                throw new BadRequestException("Email and password are required");
            }
            String email = registrationDto.getEmail().trim().toLowerCase();
            if (userRepository.existsByEmail(email)) {
                throw new BadRequestException("Email is already registered");
            }
            User user = modelMapper.map(registrationDto, User.class);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
            user.setRole(UserRole.USER);
            user.setEnabled(true);
            User savedUser = userRepository.save(user);
            UserDto userDto = modelMapper.map(savedUser, UserDto.class);
            String token = jwtUtil.generateToken(savedUser);
            JwtResponse jwtResponse = new JwtResponse(token, userDto);
            return ApiResponse.success("User registered successfully", jwtResponse);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Registration failed", e);
            throw new BadRequestException("Registration failed");
        }
    }
    
    public ApiResponse<JwtResponse> login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        
        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        
        JwtResponse jwtResponse = new JwtResponse(token, userDto);
        return ApiResponse.success("Login successful", jwtResponse);
    }
    
    public ApiResponse<UserDto> getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return ApiResponse.success("User retrieved successfully", userDto);
    }
    
    public ApiResponse<UserDto> updateProfile(String email, UserRegistrationDto updateDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Update only non-null fields
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getDateOfBirth() != null) {
            user.setDateOfBirth(updateDto.getDateOfBirth());
        }
        if (updateDto.getGender() != null) {
            user.setGender(updateDto.getGender());
        }
        if (updateDto.getHeight() != null) {
            user.setHeight(updateDto.getHeight());
        }
        if (updateDto.getWeight() != null) {
            user.setWeight(updateDto.getWeight());
        }
        if (updateDto.getActivityLevel() != null) {
            user.setActivityLevel(updateDto.getActivityLevel());
        }
        if (updateDto.getFitnessGoal() != null) {
            user.setFitnessGoal(updateDto.getFitnessGoal());
        }
        
        User updatedUser = userRepository.save(user);
        UserDto userDto = modelMapper.map(updatedUser, UserDto.class);
        
        return ApiResponse.success("Profile updated successfully", userDto);
    }
    
    public ApiResponse<UserDto> createAdminUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }
        
        User user = modelMapper.map(registrationDto, User.class);
    user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
    user.setRole(UserRole.ADMIN);
        user.setEnabled(true);
        
        User savedUser = userRepository.save(user);
        UserDto userDto = modelMapper.map(savedUser, UserDto.class);
        
        return ApiResponse.success("Admin user created successfully", userDto);
    }
}
