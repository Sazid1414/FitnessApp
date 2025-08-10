import api from './api';
import { ApiResponse } from '../types';

export interface FitnessRecommendation {
  basalMetabolicRate: number;
  dailyCalorieNeeds: number;
  bodyMassIndex: number;
  bmiCategory: string;
  dailyWaterIntake: number;
  macroSplit: Record<string, number>;
  workoutRecommendations: string[];
}

export interface BmiResult { bmi: number; category: string; }
export interface WaterIntakeResult { dailyIntakeML: number; }
export interface FitnessMetrics { bmr: number; dailyCalories: number; targetCalories: number; bmi: number; }

const prefix = '/fitness';

export const fitnessAPI = {
  getRecommendations: () => api.get<ApiResponse<FitnessRecommendation>>(`${prefix}/recommendations`),
  calculateBMI: (height: number, weight: number) => api.get<ApiResponse<BmiResult>>(`${prefix}/bmi`, { params: { height, weight } }),
  calculateWater: (weight: number) => api.get<ApiResponse<WaterIntakeResult>>(`${prefix}/water-intake`, { params: { weight } }),
  getMetrics: () => api.get<ApiResponse<FitnessMetrics>>(`${prefix}/metrics`),
};
