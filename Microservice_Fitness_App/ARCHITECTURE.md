# Fitness App Microservices Architecture Diagram

```
                                    ┌─────────────────┐
                                    │   Client App    │
                                    │  (Frontend)     │
                                    └─────────┬───────┘
                                              │ HTTP Requests
                                              ▼
                                    ┌─────────────────┐
                                    │  NGINX Proxy    │
                                    │   Port: 80      │
                                    └─────────┬───────┘
                                              │ Route & Load Balance
                         ┌────────────────────┼────────────────────┐
                         ▼                    ▼                    ▼
               ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
               │ Authentication  │  │  User Service   │  │ Workout Service │
               │    Service      │  │   Port: 8082    │  │   Port: 8083    │
               │   Port: 8081    │  │                 │  │                 │
               │                 │  │ - User Profile  │  │ - Workout CRUD  │
               │ - Registration  │  │ - Preferences   │  │ - Exercise Mgmt │
               │ - Login/Logout  │  │ - Personal Info │  │ - Progress Track│
               │ - JWT Tokens    │  │                 │  │                 │
               │ - OAuth2        │  └─────────┬───────┘  └─────────┬───────┘
               └─────────┬───────┘            │                    │
                         │                    │                    │
                         │                    ▼                    ▼
                         │          ┌─────────────────┐  ┌─────────────────┐
                         │          │ PostgreSQL DB   │  │ PostgreSQL DB   │
                         │          │   (userdb)      │  │  (workoutdb)    │
                         │          └─────────────────┘  └─────────────────┘
                         │
                         ▼
               ┌─────────────────┐
               │ PostgreSQL DB   │
               │   (authdb)      │
               └─────────────────┘

                         
                         ▼                    ▼
               ┌─────────────────┐  ┌─────────────────┐
               │ Nutrition Svc   │  │Recommendation   │
               │  Port: 8084     │  │    Service      │
               │                 │  │  Port: 8085     │
               │ - Food Logging  │  │                 │
               │ - Calorie Count │  │ - AI Integration│
               │ - Meal Planning │  │ - Fitness Calc  │
               │ - Nutrition     │  │ - BMI/BMR Calc  │
               │   Tracking      │  │ - Personalized │
               └─────────┬───────┘  │   Recommendations│
                         │          └─────────────────┘
                         ▼
               ┌─────────────────┐
               │ PostgreSQL DB   │
               │ (nutritiondb)   │
               └─────────────────┘

## Service Communication Flow

1. Client → NGINX → Routes to appropriate service
2. All services validate JWT tokens via Authentication Service  
3. Services communicate internally for data aggregation
4. Recommendation Service calls other services for comprehensive analysis

## API Endpoints

### Authentication Service (/api/v1/auth/)
- POST /register
- POST /login  
- GET /me
- PUT /profile
- POST /validate

### User Service (/api/v1/users/)
- GET /profile
- PUT /profile
- GET /preferences
- PUT /preferences

### Workout Service (/api/v1/workouts/)
- GET /
- POST /
- GET /{id}
- PUT /{id}  
- DELETE /{id}
- PUT /{id}/complete

### Nutrition Service (/api/v1/nutrition/)
- GET /logs
- POST /logs
- GET /logs/{id}
- PUT /logs/{id}
- DELETE /logs/{id}

### Recommendation Service (/api/v1/fitness/, /api/v1/ai/)
- GET /recommendations
- GET /bmi
- GET /water-intake
- GET /metrics
- POST /ai/workout-recommendation
- POST /ai/nutrition-advice

## Database Schema Distribution

### AuthDB
- users (authentication data only)
- roles, permissions

### UserDB  
- user_profiles (personal information)
- user_preferences
- user_settings

### WorkoutDB
- workouts
- exercises  
- workout_exercises
- workout_sessions

### NutritionDB
- nutrition_logs
- food_entries
- meals
- nutritional_goals
```
