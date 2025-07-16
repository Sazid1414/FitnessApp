// API Types
export interface ApiResponse<T> {
  status: string;
  message: string;
  data: T;
}

// User Types
export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  dateOfBirth?: string;
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
  height?: number;
  weight?: number;
  activityLevel?: 'SEDENTARY' | 'LIGHTLY_ACTIVE' | 'MODERATELY_ACTIVE' | 'VERY_ACTIVE' | 'EXTREMELY_ACTIVE';
  fitnessGoal?: 'LOSE_WEIGHT' | 'GAIN_WEIGHT' | 'MAINTAIN_WEIGHT' | 'BUILD_MUSCLE' | 'IMPROVE_ENDURANCE' | 'GENERAL_FITNESS';
  profilePictureUrl?: string;
  role: 'USER' | 'ADMIN';
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UserRegistration {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  dateOfBirth?: string;
  gender?: User['gender'];
  height?: number;
  weight?: number;
  activityLevel?: User['activityLevel'];
  fitnessGoal?: User['fitnessGoal'];
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface JwtResponse {
  token: string;
  type: string;
  user: User;
}

// Workout Types
export interface Workout {
  id?: number;
  name: string;
  description?: string;
  type: 'CARDIO' | 'STRENGTH' | 'FLEXIBILITY' | 'SPORTS' | 'YOGA' | 'PILATES' | 'HIIT' | 'CROSSFIT' | 'RUNNING' | 'CYCLING' | 'SWIMMING';
  durationMinutes?: number;
  caloriesBurned?: number;
  notes?: string;
  intensity?: 'LOW' | 'MODERATE' | 'HIGH' | 'VERY_HIGH';
  workoutDate: string;
  completed: boolean;
  createdAt?: string;
  updatedAt?: string;
  userId?: number;
  exercises?: Exercise[];
}

export interface Exercise {
  id?: number;
  name: string;
  description?: string;
  sets?: number;
  reps?: number;
  weight?: number;
  durationSeconds?: number;
  distance?: number;
  caloriesBurned?: number;
  notes?: string;
  createdAt?: string;
  workoutId?: number;
}

// AI Types
export interface AIRequest {
  prompt: string;
  userProfile?: string;
  requestType?: string;
}

export interface AIResponse {
  response: string;
  type: string;
  success: boolean;
}

// Navigation Types
export interface NavItem {
  name: string;
  href: string;
  icon?: React.ComponentType<any>;
  current?: boolean;
}
