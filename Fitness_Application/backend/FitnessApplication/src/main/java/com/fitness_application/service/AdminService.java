package com.fitness_application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fitness_application.domain.enums.UserRole;
import com.fitness_application.dto.ApiResponse;
import com.fitness_application.dto.UserDto;
import com.fitness_application.model.User;
import com.fitness_application.repository.GoalRepository;
import com.fitness_application.repository.UserRepository;
import com.fitness_application.repository.WorkoutRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final GoalRepository goalRepository;
    private final ModelMapper modelMapper;
    
    public ApiResponse<List<UserDto>> getAllUsers(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.findAll(pageable);
            
            List<UserDto> userDtos = userPage.getContent().stream()
                    .map(user -> modelMapper.map(user, UserDto.class))
                    .collect(Collectors.toList());
            
            return ApiResponse.success("Users retrieved successfully", userDtos);
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve users: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserDto> getUserById(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return ApiResponse.success("User retrieved successfully", userDto);
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve user: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserDto> updateUserRole(Long userId, String roleName) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            UserRole role = UserRole.valueOf(roleName.toUpperCase());
            user.setRole(role);
            user = userRepository.save(user);
            
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return ApiResponse.success("User role updated successfully", userDto);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("Invalid role: " + roleName + ". Valid roles: USER, ADMIN");
        } catch (Exception e) {
            return ApiResponse.error("Failed to update user role: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserDto> updateUserStatus(Long userId, boolean enabled) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            user.setEnabled(enabled);
            user = userRepository.save(user);
            
            UserDto userDto = modelMapper.map(user, UserDto.class);
            String message = enabled ? "User enabled successfully" : "User disabled successfully";
            return ApiResponse.success(message, userDto);
        } catch (Exception e) {
            return ApiResponse.error("Failed to update user status: " + e.getMessage());
        }
    }
    
    public ApiResponse<Void> deleteUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            // Check if user is admin - don't allow deletion of admin users
            if (user.getRole() == UserRole.ADMIN) {
                return ApiResponse.error("Cannot delete admin users");
            }
            
            userRepository.delete(user);
            return ApiResponse.success("User deleted successfully", null);
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete user: " + e.getMessage());
        }
    }
    
    public ApiResponse<Object> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // User statistics
            long totalUsers = userRepository.count();
            long totalAdmins = userRepository.countByRole(UserRole.ADMIN);
            long totalRegularUsers = userRepository.countByRole(UserRole.USER);
            long activeUsers = userRepository.countByEnabled(true);
            
            // Workout statistics
            long totalWorkouts = workoutRepository.count();
            long completedWorkouts = workoutRepository.countByCompleted(true);
            
            // Goal statistics
            long totalGoals = goalRepository.count();
            
            stats.put("totalUsers", totalUsers);
            stats.put("totalAdmins", totalAdmins);
            stats.put("totalRegularUsers", totalRegularUsers);
            stats.put("activeUsers", activeUsers);
            stats.put("totalWorkouts", totalWorkouts);
            stats.put("completedWorkouts", completedWorkouts);
            stats.put("totalGoals", totalGoals);
            
            return ApiResponse.success("Dashboard statistics retrieved successfully", stats);
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve dashboard statistics: " + e.getMessage());
        }
    }
}
