# Microservices Testing Guide

## Quick Start

1. **Build all services:**
   ```bash
   # Windows
   build-all.bat
   
   # Linux/Mac
   chmod +x build-all.sh
   ./build-all.sh
   ```

2. **Start all services:**
   ```bash
   docker-compose up --build
   ```

3. **Access Points:**
   - API Gateway: http://localhost
   - Authentication Service: http://localhost:8081
   - User Service: http://localhost:8082  
   - Workout Service: http://localhost:8083
   - Nutrition Service: http://localhost:8084
   - Recommendation Service: http://localhost:8085

## Swagger Documentation

Each service provides Swagger UI for testing:

- Authentication: http://localhost:8081/swagger-ui.html
- User Service: http://localhost:8082/swagger-ui.html
- Workout Service: http://localhost:8083/swagger-ui.html
- Nutrition Service: http://localhost:8084/swagger-ui.html
- Recommendation Service: http://localhost:8085/swagger-ui.html

## API Testing Flow

### 1. User Registration and Authentication

**Register a new user:**
```bash
curl -X POST http://localhost/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "1990-01-01",
    "gender": "MALE",
    "height": 180,
    "weight": 75,
    "activityLevel": "MODERATELY_ACTIVE",
    "fitnessGoal": "MAINTAIN_WEIGHT"
  }'
```

**Login:**
```bash
curl -X POST http://localhost/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

Save the JWT token from the response for subsequent requests.

### 2. User Profile Management

**Get user profile:**
```bash
curl -X GET http://localhost/api/v1/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Update user profile:**
```bash
curl -X PUT http://localhost/api/v1/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John Updated",
    "lastName": "Doe",
    "height": 185,
    "weight": 80
  }'
```

### 3. Workout Management

**Create a workout:**
```bash
curl -X POST http://localhost/api/v1/workouts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Morning Cardio",
    "description": "30-minute cardio session",
    "type": "CARDIO",
    "durationMinutes": 30,
    "intensity": "MODERATE",
    "workoutDate": "2025-08-15T07:00:00",
    "exercises": [
      {
        "name": "Running",
        "category": "CARDIO",
        "durationSeconds": 1800,
        "distance": 5000
      }
    ]
  }'
```

**Get user workouts:**
```bash
curl -X GET "http://localhost/api/v1/workouts?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Nutrition Tracking

**Log a meal:**
```bash
curl -X POST http://localhost/api/v1/nutrition/logs \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "logDate": "2025-08-15",
    "mealType": "BREAKFAST",
    "foodEntries": [
      {
        "foodName": "Oatmeal",
        "quantity": 1,
        "unit": "cup",
        "calories": 150,
        "protein": 5,
        "carbs": 30,
        "fat": 3
      }
    ]
  }'
```

### 5. Fitness Recommendations

**Get fitness recommendations:**
```bash
curl -X GET http://localhost/api/v1/fitness/recommendations \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Calculate BMI:**
```bash
curl -X GET "http://localhost/api/v1/fitness/bmi?height=180&weight=75" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Get AI workout recommendation:**
```bash
curl -X POST http://localhost/api/v1/ai/workout-recommendation \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "I want a 30-minute strength workout for building muscle"
  }'
```

## Health Checks

Each service provides health endpoints:

```bash
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8082/actuator/health  # User Service
curl http://localhost:8083/actuator/health  # Workout Service
curl http://localhost:8084/actuator/health  # Nutrition Service
curl http://localhost:8085/actuator/health  # Recommendation Service
```

## Microservice Communication Test

The services communicate with each other internally. For example:

1. **Authentication Service** validates JWT tokens for all services
2. **User Service** stores user profiles referenced by other services
3. **Recommendation Service** calls other services to generate comprehensive recommendations
4. **NGINX** routes all requests through the reverse proxy

## Database Access

Each service uses H2 in-memory database for easy testing:

- Auth DB Console: http://localhost:8081/h2-console
- User DB Console: http://localhost:8082/h2-console
- Workout DB Console: http://localhost:8083/h2-console
- Nutrition DB Console: http://localhost:8084/h2-console

Use these credentials:
- JDBC URL: `jdbc:h2:mem:testdb` (replace testdb with service-specific name)
- Username: `sa`
- Password: `password`

## Troubleshooting

1. **Service not starting:** Check Docker logs
   ```bash
   docker-compose logs authentication-service
   ```

2. **Port conflicts:** Stop other services using ports 8081-8085

3. **Authentication errors:** Make sure JWT token is valid and not expired

4. **Database errors:** Check H2 console for table creation issues

## Load Testing

Use tools like Apache Bench or JMeter for load testing:

```bash
# Test authentication endpoint
ab -n 100 -c 10 -T application/json -p register.json http://localhost/api/v1/auth/register
```

## Postman Collection

Import the Swagger documentation into Postman for easy testing:
1. Go to each service's swagger-ui endpoint
2. Copy the OpenAPI JSON/YAML
3. Import into Postman

## Production Considerations

- Replace H2 with PostgreSQL for production
- Implement proper JWT secret management
- Add rate limiting and security headers
- Configure proper logging and monitoring
- Implement circuit breakers for service communication
