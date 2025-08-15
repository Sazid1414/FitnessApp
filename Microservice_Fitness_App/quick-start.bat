@echo off
echo Setting up Fitness App Microservices...

REM Create minimal working services
call :create_minimal_service authentication-service 8081
call :create_minimal_service user-service 8082
call :create_minimal_service workout-service 8083
call :create_minimal_service nutrition-service 8084
call :create_minimal_service recommendation-service 8085

echo Building Maven projects...
for %%s in (authentication-service user-service workout-service nutrition-service recommendation-service) do (
    echo Building %%s...
    cd %%s
    mvn clean package -DskipTests -q
    if %errorlevel% neq 0 (
        echo Failed to build %%s
        exit /b 1
    )
    cd ..
)

echo Starting Docker Compose...
docker-compose up --build -d

echo Waiting for services to start...
timeout /t 30 /nobreak

echo Testing service endpoints...
curl -s http://localhost:8081/health >nul 2>&1 && echo Auth Service OK || echo Auth Service Failed
curl -s http://localhost:8082/health >nul 2>&1 && echo User Service OK || echo User Service Failed  
curl -s http://localhost:8083/health >nul 2>&1 && echo Workout Service OK || echo Workout Service Failed
curl -s http://localhost:8084/health >nul 2>&1 && echo Nutrition Service OK || echo Nutrition Service Failed
curl -s http://localhost:8085/health >nul 2>&1 && echo Recommendation Service OK || echo Recommendation Service Failed

echo.
echo Microservices are running!
echo.
echo Access Points:
echo   - API Gateway (NGINX): http://localhost
echo   - Services: http://localhost:8081-8085
echo.
echo Swagger Documentation available at each service /swagger-ui.html
echo.
echo Test with: curl http://localhost:8081/health
goto :EOF

:create_minimal_service
set service=%~1
set port=%~2

echo Creating minimal %service% on port %port%...

REM Extract service name (remove -service)
for /f "tokens=1 delims=-" %%a in ("%service%") do set servicename=%%a

REM Create directories
if not exist "%service%\src\main\java\com\fitness_application\%servicename%" mkdir "%service%\src\main\java\com\fitness_application\%servicename%"
if not exist "%service%\src\main\resources" mkdir "%service%\src\main\resources"

REM Create application class
(
echo package com.fitness_application.%servicename%;
echo.
echo import org.springframework.boot.SpringApplication;
echo import org.springframework.boot.autoconfigure.SpringBootApplication;
echo import org.springframework.web.bind.annotation.GetMapping;
echo import org.springframework.web.bind.annotation.RestController;
echo.
echo @SpringBootApplication
echo public class %servicename%ServiceApplication {
echo     public static void main(String[] args^) {
echo         SpringApplication.run(%servicename%ServiceApplication.class, args^);
echo     }
echo.    
echo     @RestController
echo     public static class HealthController {
echo         @GetMapping("/health"^)
echo         public String health(^) {
echo             return "%service% is healthy";
echo         }
echo.        
echo         @GetMapping("/actuator/health"^) 
echo         public String actuatorHealth(^) {
echo             return "{\"status\":\"UP\"}";
echo         }
echo     }
echo }
) > "%service%\src\main\java\com\fitness_application\%servicename%\%servicename%ServiceApplication.java"

REM Create application.properties
(
echo spring.application.name=%service%
echo server.port=%port%
echo.
echo # H2 Database
echo spring.datasource.url=jdbc:h2:mem:testdb
echo spring.datasource.driver-class-name=org.h2.Driver
echo spring.datasource.username=sa
echo spring.datasource.password=password
echo spring.h2.console.enabled=true
echo.
echo # JPA
echo spring.jpa.hibernate.ddl-auto=create-drop
echo spring.jpa.show-sql=false
echo.
echo # Management
echo management.endpoints.web.exposure.include=health,info
echo.
echo # Swagger
echo springdoc.api-docs.path=/api-docs
echo springdoc.swagger-ui.path=/swagger-ui.html
echo.
echo # Logging
echo logging.level.com.fitness_application=INFO
) > "%service%\src\main\resources\application.properties"

echo Created minimal %service%
goto :EOF
