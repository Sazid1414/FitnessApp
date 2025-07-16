import axios, { AxiosResponse } from 'axios';
import { 
  ApiResponse, 
  User, 
  UserRegistration, 
  LoginRequest, 
  JwtResponse, 
  Workout,
  AIRequest,
  AIResponse
} from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  register: (userData: UserRegistration): Promise<AxiosResponse<ApiResponse<JwtResponse>>> => {
    return api.post('/auth/register', userData);
  },

  login: (credentials: LoginRequest): Promise<AxiosResponse<ApiResponse<JwtResponse>>> => {
    return api.post('/auth/login', credentials);
  },

  getCurrentUser: (): Promise<AxiosResponse<ApiResponse<User>>> => {
    return api.get('/auth/me');
  },

  updateProfile: (userData: Partial<UserRegistration>): Promise<AxiosResponse<ApiResponse<User>>> => {
    return api.put('/auth/profile', userData);
  },
};

// Workout API
export const workoutAPI = {
  getWorkouts: (page = 0, size = 10): Promise<AxiosResponse<ApiResponse<Workout[]>>> => {
    return api.get(`/workouts?page=${page}&size=${size}`);
  },

  getWorkout: (id: number): Promise<AxiosResponse<ApiResponse<Workout>>> => {
    return api.get(`/workouts/${id}`);
  },

  createWorkout: (workout: Workout): Promise<AxiosResponse<ApiResponse<Workout>>> => {
    return api.post('/workouts', workout);
  },

  updateWorkout: (id: number, workout: Workout): Promise<AxiosResponse<ApiResponse<Workout>>> => {
    return api.put(`/workouts/${id}`, workout);
  },

  deleteWorkout: (id: number): Promise<AxiosResponse<ApiResponse<void>>> => {
    return api.delete(`/workouts/${id}`);
  },

  completeWorkout: (id: number): Promise<AxiosResponse<ApiResponse<Workout>>> => {
    return api.put(`/workouts/${id}/complete`);
  },
};

// AI API
export const aiAPI = {
  getWorkoutRecommendation: (request: AIRequest): Promise<AxiosResponse<ApiResponse<AIResponse>>> => {
    return api.post('/ai/workout-recommendation', request);
  },

  getNutritionAdvice: (request: AIRequest): Promise<AxiosResponse<ApiResponse<AIResponse>>> => {
    return api.post('/ai/nutrition-advice', request);
  },

  getGeneralAdvice: (request: AIRequest): Promise<AxiosResponse<ApiResponse<AIResponse>>> => {
    return api.post('/ai/general-advice', request);
  },
};

export default api;
