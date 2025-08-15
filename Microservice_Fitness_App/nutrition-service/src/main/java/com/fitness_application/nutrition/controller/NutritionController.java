package com.fitness_application.nutrition.controller;

import com.fitness_application.nutrition.dto.ApiResponse;
import com.fitness_application.nutrition.dto.FoodEntryDto;
import com.fitness_application.nutrition.dto.NutritionLogDto;
import com.fitness_application.nutrition.service.NutritionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
public class NutritionController {

    @Autowired
    private NutritionService nutritionService;

    @GetMapping("/logs/{userId}")
    public ResponseEntity<List<NutritionLogDto>> getNutritionLogs(@PathVariable Long userId) {
        try {
            List<NutritionLogDto> logs = nutritionService.getNutritionLogsForUser(userId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/logs/{userId}/date/{date}")
    public ResponseEntity<NutritionLogDto> getNutritionLogByDate(@PathVariable Long userId, @PathVariable String date) {
        try {
            NutritionLogDto log = nutritionService.getNutritionLogByUserAndDate(userId, date);
            if (log != null) {
                return ResponseEntity.ok(log);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logs")
    public ResponseEntity<NutritionLogDto> createNutritionLog(@RequestBody NutritionLogDto nutritionLogDto) {
        try {
            NutritionLogDto createdLog = nutritionService.createNutritionLog(nutritionLogDto);
            return ResponseEntity.ok(createdLog);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/logs/{logId}/user/{userId}")
    public ResponseEntity<NutritionLogDto> updateNutritionLog(@PathVariable Long logId, @PathVariable Long userId, 
                                                             @RequestBody NutritionLogDto nutritionLogDto) {
        try {
            ApiResponse<NutritionLogDto> response = nutritionService.updateNutritionLog(logId, userId, nutritionLogDto);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response.getData());
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/logs/{logId}/user/{userId}")
    public ResponseEntity<Void> deleteNutritionLog(@PathVariable Long logId, @PathVariable Long userId) {
        try {
            nutritionService.deleteNutritionLog(logId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/logs/{logId}/entries")
    public ResponseEntity<List<FoodEntryDto>> getFoodEntries(@PathVariable Long logId) {
        try {
            List<FoodEntryDto> entries = nutritionService.getFoodEntriesForLog(logId);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/entries")
    public ResponseEntity<FoodEntryDto> addFoodEntry(@RequestBody FoodEntryDto foodEntryDto) {
        try {
            FoodEntryDto createdEntry = nutritionService.addFoodEntry(foodEntryDto);
            return ResponseEntity.ok(createdEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/entries/{entryId}")
    public ResponseEntity<FoodEntryDto> updateFoodEntry(@PathVariable Long entryId, 
                                                       @RequestBody FoodEntryDto foodEntryDto) {
        try {
            FoodEntryDto updatedEntry = nutritionService.updateFoodEntry(entryId, foodEntryDto);
            return ResponseEntity.ok(updatedEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/entries/{entryId}")
    public ResponseEntity<Void> deleteFoodEntry(@PathVariable Long entryId) {
        try {
            nutritionService.deleteFoodEntry(entryId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
