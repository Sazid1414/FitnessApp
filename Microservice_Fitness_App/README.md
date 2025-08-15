# Fitness App Microservices Architecture

This project demonstrates the decomposition of a monolithic Fitness Application into microservices with NGINX as a reverse proxy.

## Architecture Overview

### Services

1. **Authentication Service** (Port 8081)
   - Handles user registration, login, and JWT token management
   - OAuth2 integration for social logins
   - User role management

2. **User Service** (Port 8082)
   - Manages user profiles and personal information
   - User preferences and settings
   - Profile updates and data management

3. **Workout Service** (Port 8083)
   - Workout creation and management
   - Exercise tracking
   - Workout history and analytics

4. **Nutrition Service** (Port 8084)
   - Food logging and nutrition tracking
   - Calorie counting
   - Nutritional goal management

5. **Recommendation Service** (Port 8085)
   - AI-powered fitness recommendations
   - Personalized workout suggestions
   - Nutrition advice based on user goals
   - BMI calculations and fitness metrics

6. **NGINX Reverse Proxy** (Port 80)
   - Single entry point for all client requests
   - Load balancing and routing
   - SSL termination (when configured)

### Database Architecture

Each service has its own dedicated PostgreSQL database:
- `authdb` - Authentication data
- `userdb` - User profiles and personal data
- `workoutdb` - Workout and exercise data
- `nutritiondb` - Nutrition and food logging data

## API Gateway Routes

All requests go through NGINX reverse proxy on port 80:

```
/api/v1/auth/*       → authentication-service:8081
/api/v1/users/*      → user-service:8082
/api/v1/workouts/*   → workout-service:8083
/api/v1/nutrition/*  → nutrition-service:8084
/api/v1/fitness/*    → recommendation-service:8085
/api/v1/ai/*         → recommendation-service:8085
```

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 21 (for local development)
- Maven (for building services)

### Running with Docker Compose

1. Clone the repository
2. Navigate to the microservice directory
3. Build and run all services:

```bash
docker-compose up --build
```

This will start:
- All microservices
- PostgreSQL databases
- NGINX reverse proxy

### Access Points

- **API Gateway**: http://localhost
- **Swagger UI**: 
  - Auth Service: http://localhost:8081/swagger-ui.html
  - User Service: http://localhost:8082/swagger-ui.html
  - Workout Service: http://localhost:8083/swagger-ui.html
  - Nutrition Service: http://localhost:8084/swagger-ui.html
  - Recommendation Service: http://localhost:8085/swagger-ui.html

### Building Individual Services

Each service can be built independently:

```bash
cd authentication-service
mvn clean package
docker build -t fitness-auth-service .

cd ../user-service
mvn clean package
docker build -t fitness-user-service .

# Repeat for other services...
```

## Service Communication

Services communicate with each other through HTTP REST APIs. JWT tokens are passed between services for authentication and authorization.

### Authentication Flow
1. User authenticates with Authentication Service
2. Authentication Service returns JWT token
3. Client includes JWT token in subsequent requests
4. Each service validates JWT token with Authentication Service
5. Services can call each other using internal service URLs

## Development

### Adding a New Service

1. Create new directory with Spring Boot project structure
2. Add pom.xml with required dependencies
3. Create Dockerfile
4. Add service to docker-compose.yml
5. Update nginx.conf with new route

### Environment Variables

Configure the following environment variables in docker-compose.yml:
- Database connections
- Service URLs
- AI API keys
- JWT secrets

## Production Considerations

- Replace H2 with PostgreSQL for all services
- Implement proper secret management
- Add SSL certificates to NGINX
- Configure proper logging and monitoring
- Implement circuit breakers for service communication
- Add service discovery (Consul, Eureka)
- Implement distributed tracing
- Add rate limiting and security headers

## Testing

Each service includes unit and integration tests. Run tests with:

```bash
mvn test
```

For integration testing across services, use Docker Compose test environment.

## Monitoring and Health Checks

All services include Spring Boot Actuator for health checks:
- `/actuator/health` - Service health status
- `/actuator/info` - Service information

## Security

- JWT-based authentication
- Role-based access control
- Service-to-service communication secured
- Input validation on all endpoints
- CORS configuration for frontend integration
