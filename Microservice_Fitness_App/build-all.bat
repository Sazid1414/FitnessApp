@echo off
REM Build all microservices for Windows

echo Building Fitness App Microservices...

REM Function to build a service
call :build_service "authentication-service"
call :build_service "user-service"
call :build_service "workout-service"
call :build_service "nutrition-service"
call :build_service "recommendation-service"

echo All services built successfully!
echo Run 'docker-compose up --build' to start all services
goto :EOF

:build_service
set service=%~1
echo Building %service%...
cd %service%
mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo Failed to build %service%
    exit /b 1
)
echo %service% built successfully
cd ..
goto :EOF
