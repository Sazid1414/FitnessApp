package com.fitness_application.nutrition.service;

import com.fitness_application.nutrition.dto.ApiResponse;
import com.fitness_application.nutrition.dto.NutritionLogDto;
import com.fitness_application.nutrition.dto.FoodEntryDto;
import com.fitness_application.nutrition.model.NutritionLog;
import com.fitness_application.nutrition.model.FoodEntry;
import com.fitness_application.nutrition.repository.NutritionLogRepository;
import com.fitness_application.nutrition.repository.FoodEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NutritionService {

    private final NutritionLogRepository nutritionLogRepository;
    private final FoodEntryRepository foodEntryRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<NutritionLogDto> createNutritionLog(Long userId, NutritionLogDto nutritionLogDto) {
        try {
            log.info("Creating nutrition log for user: {}", userId);

            // Check if log already exists for this date
            if (nutritionLogRepository.findByUserIdAndLogDate(userId, nutritionLogDto.getLogDate()).isPresent()) {
                return new ApiResponse<>(false, "Nutrition log already exists for this date", null);
            }

            NutritionLog nutritionLog = modelMapper.map(nutritionLogDto, NutritionLog.class);
            nutritionLog.setUserId(userId);

            NutritionLog savedLog = nutritionLogRepository.save(nutritionLog);

            // Handle food entries if provided
            if (nutritionLogDto.getFoodEntries() != null && !nutritionLogDto.getFoodEntries().isEmpty()) {
                List<FoodEntry> foodEntries = new ArrayList<>();
                
                for (FoodEntryDto foodDto : nutritionLogDto.getFoodEntries()) {
                    FoodEntry foodEntry = convertDtoToFoodEntry(foodDto, savedLog);
                    foodEntries.add(foodEntry);
                }

                foodEntryRepository.saveAll(foodEntries);
                savedLog.setFoodEntries(foodEntries);
                savedLog.calculateTotals();
                savedLog = nutritionLogRepository.save(savedLog);
            }

            NutritionLogDto responseDto = convertToNutritionLogDto(savedLog);

            return new ApiResponse<>(true, "Nutrition log created successfully", responseDto);

        } catch (Exception e) {
            log.error("Error creating nutrition log for user {}: {}", userId, e.getMessage(), e);
            return new ApiResponse<>(false, "Error creating nutrition log: " + e.getMessage(), null);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<NutritionLogDto>> getNutritionLogs(Long userId, int page, int size) {
        try {
            log.info("Fetching nutrition logs for user: {}, page: {}, size: {}", userId, page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<NutritionLog> logPage = nutritionLogRepository.findByUserIdOrderByLogDateDesc(userId, pageable);

            List<NutritionLogDto> nutritionLogDtos = logPage.getContent().stream()
                    .map(nutritionLog -> {
                        List<FoodEntry> foodEntries = foodEntryRepository.findByNutritionLogIdOrderById(nutritionLog.getId());
                        return convertToNutritionLogDtoWithEntries(nutritionLog, foodEntries);
                    })
                    .collect(Collectors.toList());

            return new ApiResponse<>(true, "Nutrition logs retrieved successfully", nutritionLogDtos);

        } catch (Exception e) {
            log.error("Error fetching nutrition logs for user {}: {}", userId, e.getMessage(), e);
            return new ApiResponse<>(false, "Error fetching nutrition logs: " + e.getMessage(), null);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<NutritionLogDto> getNutritionLogById(Long userId, Long logId) {
        try {
            log.info("Fetching nutrition log {} for user: {}", logId, userId);

            NutritionLog nutritionLogEntity = nutritionLogRepository.findByIdAndUserId(logId, userId)
                    .orElseThrow(() -> new RuntimeException("Nutrition log not found"));

            List<FoodEntry> foodEntries = foodEntryRepository.findByNutritionLogIdOrderById(logId);
            NutritionLogDto nutritionLogDto = convertToNutritionLogDtoWithEntries(nutritionLogEntity, foodEntries);

            return new ApiResponse<>(true, "Nutrition log retrieved successfully", nutritionLogDto);

        } catch (Exception e) {
            log.error("Error fetching nutrition log {} for user {}: {}", logId, userId, e.getMessage(), e);
            return new ApiResponse<>(false, "Error fetching nutrition log: " + e.getMessage(), null);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<NutritionLogDto> getNutritionLogByDate(Long userId, LocalDate date) {
        try {
            log.info("Fetching nutrition log for user: {} on date: {}", userId, date);

            NutritionLog nutritionLogEntity = nutritionLogRepository.findByUserIdAndLogDate(userId, date)
                    .orElseThrow(() -> new RuntimeException("Nutrition log not found for date: " + date));

            List<FoodEntry> foodEntries = foodEntryRepository.findByNutritionLogIdOrderById(nutritionLogEntity.getId());
            NutritionLogDto nutritionLogDto = convertToNutritionLogDtoWithEntries(nutritionLogEntity, foodEntries);

            return new ApiResponse<>(true, "Nutrition log retrieved successfully", nutritionLogDto);

        } catch (Exception e) {
            log.error("Error fetching nutrition log for user {} on date {}: {}", userId, date, e.getMessage(), e);
            return new ApiResponse<>(false, "Error fetching nutrition log: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponse<NutritionLogDto> updateNutritionLog(Long logId, Long userId, NutritionLogDto updatedNutritionLogDto) {
        try {
            log.info("Updating nutrition log {} for user: {}", logId, userId);

            NutritionLog nutritionLog = nutritionLogRepository.findByIdAndUserId(logId, userId)
                    .orElseThrow(() -> new RuntimeException("Nutrition log not found"));

            // Update basic fields
            if (updatedNutritionLogDto.getLogDate() != null) {
                nutritionLog.setLogDate(updatedNutritionLogDto.getLogDate());
            }
            if (updatedNutritionLogDto.getNotes() != null) {
                nutritionLog.setNotes(updatedNutritionLogDto.getNotes());
            }

            // Handle food entries update
            if (updatedNutritionLogDto.getFoodEntries() != null) {
                // Delete existing food entries
                foodEntryRepository.deleteByNutritionLogId(logId);

                // Add new food entries
                List<FoodEntry> newFoodEntries = new ArrayList<>();
                for (FoodEntryDto foodDto : updatedNutritionLogDto.getFoodEntries()) {
                    FoodEntry foodEntry = convertDtoToFoodEntry(foodDto, nutritionLog);
                    newFoodEntries.add(foodEntry);
                }

                foodEntryRepository.saveAll(newFoodEntries);
                nutritionLog.setFoodEntries(newFoodEntries);
                nutritionLog.calculateTotals();
            }

            NutritionLog savedLog = nutritionLogRepository.save(nutritionLog);
            NutritionLogDto responseDto = convertToNutritionLogDto(savedLog);

            return new ApiResponse<>(true, "Nutrition log updated successfully", responseDto);

        } catch (Exception e) {
            log.error("Error updating nutrition log {} for user {}: {}", logId, userId, e.getMessage(), e);
            return new ApiResponse<>(false, "Error updating nutrition log: " + e.getMessage(), null);
        }
    }

    @Transactional
    public void deleteNutritionLog(Long logId, Long userId) {
        try {
            log.info("Deleting nutrition log {} for user: {}", logId, userId);

            NutritionLog nutritionLogEntity = nutritionLogRepository.findByIdAndUserId(logId, userId)
                    .orElseThrow(() -> new RuntimeException("Nutrition log not found"));
            
            // Delete food entries first
            foodEntryRepository.deleteByNutritionLogId(logId);
            
            // Delete log
            nutritionLogRepository.delete(nutritionLogEntity);

        } catch (Exception e) {
            log.error("Error deleting nutrition log {} for user {}: {}", logId, userId, e.getMessage(), e);
            throw new RuntimeException("Error deleting nutrition log: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> getNutritionLogCount(Long userId) {
        try {
            log.info("Getting nutrition log count for user: {}", userId);

            long count = nutritionLogRepository.countNutritionLogsByUser(userId);

            return new ApiResponse<>(true, "Nutrition log count retrieved successfully", count);

        } catch (Exception e) {
            log.error("Error getting nutrition log count for user {}: {}", userId, e.getMessage(), e);
            return new ApiResponse<>(false, "Error getting nutrition log count: " + e.getMessage(), null);
        }
    }

    // Convenience methods for controller
    @Transactional(readOnly = true)
    public List<NutritionLogDto> getNutritionLogsForUser(Long userId) {
        ApiResponse<List<NutritionLogDto>> response = getNutritionLogs(userId, 0, 100);
        return response.isSuccess() ? response.getData() : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public NutritionLogDto getNutritionLogByUserAndDate(Long userId, String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);
            ApiResponse<NutritionLogDto> response = getNutritionLogByDate(userId, date);
            return response.isSuccess() ? response.getData() : null;
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format or log not found");
        }
    }

    @Transactional
    public NutritionLogDto createNutritionLog(NutritionLogDto nutritionLogDto) {
        ApiResponse<NutritionLogDto> response = createNutritionLog(nutritionLogDto.getUserId(), nutritionLogDto);
        if (!response.isSuccess()) {
            throw new RuntimeException("Failed to create nutrition log");
        }
        return response.getData();
    }

    @Transactional(readOnly = true)
    public List<FoodEntryDto> getFoodEntriesForLog(Long logId) {
        List<FoodEntry> entries = foodEntryRepository.findByNutritionLogId(logId);
        return entries.stream().map(this::convertToFoodEntryDto).collect(Collectors.toList());
    }

    @Transactional
    public FoodEntryDto addFoodEntry(FoodEntryDto foodEntryDto) {
        try {
            // Find the nutrition log
            NutritionLog nutritionLog = nutritionLogRepository.findById(foodEntryDto.getNutritionLogId())
                    .orElseThrow(() -> new RuntimeException("Nutrition log not found"));

            FoodEntry foodEntry = convertDtoToFoodEntry(foodEntryDto, nutritionLog);
            FoodEntry savedEntry = foodEntryRepository.save(foodEntry);

            // Recalculate totals
            nutritionLog.calculateTotals();
            nutritionLogRepository.save(nutritionLog);

            return convertToFoodEntryDto(savedEntry);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add food entry");
        }
    }

    @Transactional
    public FoodEntryDto updateFoodEntry(Long entryId, FoodEntryDto foodEntryDto) {
        try {
            FoodEntry foodEntry = foodEntryRepository.findById(entryId)
                    .orElseThrow(() -> new RuntimeException("Food entry not found"));

            // Update fields
            foodEntry.setFoodName(foodEntryDto.getFoodName());
            foodEntry.setBrand(foodEntryDto.getBrand());
            foodEntry.setQuantity(foodEntryDto.getQuantity());
            foodEntry.setUnit(foodEntryDto.getUnit());
            foodEntry.setCalories(foodEntryDto.getCalories());
            foodEntry.setProtein(foodEntryDto.getProtein());
            foodEntry.setCarbs(foodEntryDto.getCarbs());
            foodEntry.setFat(foodEntryDto.getFat());
            foodEntry.setFiber(foodEntryDto.getFiber());
            foodEntry.setSugar(foodEntryDto.getSugar());
            foodEntry.setSodium(foodEntryDto.getSodium());

            FoodEntry savedEntry = foodEntryRepository.save(foodEntry);

            // Recalculate totals
            NutritionLog nutritionLog = savedEntry.getNutritionLog();
            nutritionLog.calculateTotals();
            nutritionLogRepository.save(nutritionLog);

            return convertToFoodEntryDto(savedEntry);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update food entry");
        }
    }

    @Transactional
    public void deleteFoodEntry(Long entryId) {
        try {
            FoodEntry foodEntry = foodEntryRepository.findById(entryId)
                    .orElseThrow(() -> new RuntimeException("Food entry not found"));

            NutritionLog nutritionLog = foodEntry.getNutritionLog();
            foodEntryRepository.delete(foodEntry);

            // Recalculate totals
            nutritionLog.calculateTotals();
            nutritionLogRepository.save(nutritionLog);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete food entry");
        }
    }

    // Helper methods for conversion
    private NutritionLogDto convertToNutritionLogDto(NutritionLog nutritionLog) {
        NutritionLogDto dto = modelMapper.map(nutritionLog, NutritionLogDto.class);
        if (nutritionLog.getMealType() != null) {
            dto.setMealType(nutritionLog.getMealType().toString());
        }
        return dto;
    }

    private NutritionLogDto convertToNutritionLogDtoWithEntries(NutritionLog nutritionLog, List<FoodEntry> foodEntries) {
        NutritionLogDto dto = convertToNutritionLogDto(nutritionLog);
        List<FoodEntryDto> foodEntryDtos = foodEntries.stream()
                .map(this::convertToFoodEntryDto)
                .collect(Collectors.toList());
        dto.setFoodEntries(foodEntryDtos);
        return dto;
    }

    private FoodEntryDto convertToFoodEntryDto(FoodEntry foodEntry) {
        FoodEntryDto dto = modelMapper.map(foodEntry, FoodEntryDto.class);
        if (foodEntry.getNutritionLog() != null) {
            dto.setNutritionLogId(foodEntry.getNutritionLog().getId());
        }
        return dto;
    }

    private FoodEntry convertDtoToFoodEntry(FoodEntryDto foodDto, NutritionLog nutritionLog) {
        FoodEntry foodEntry = new FoodEntry();
        foodEntry.setFoodName(foodDto.getFoodName());
        foodEntry.setBrand(foodDto.getBrand());
        foodEntry.setQuantity(foodDto.getQuantity());
        foodEntry.setUnit(foodDto.getUnit());
        foodEntry.setCalories(foodDto.getCalories());
        foodEntry.setProtein(foodDto.getProtein());
        foodEntry.setCarbs(foodDto.getCarbs());
        foodEntry.setFat(foodDto.getFat());
        foodEntry.setFiber(foodDto.getFiber());
        foodEntry.setSugar(foodDto.getSugar());
        foodEntry.setSodium(foodDto.getSodium());
        foodEntry.setCholesterol(foodDto.getCholesterol());
        foodEntry.setVitaminC(foodDto.getVitaminC());
        foodEntry.setCalcium(foodDto.getCalcium());
        foodEntry.setIron(foodDto.getIron());
        foodEntry.setBarcode(foodDto.getBarcode());
        foodEntry.setNotes(foodDto.getNotes());
        foodEntry.setNutritionLog(nutritionLog);
        return foodEntry;
    }
}
