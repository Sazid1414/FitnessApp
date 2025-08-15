#!/bin/bash

# Build all microservices
echo "Building Fitness App Microservices..."

# Function to build a service
build_service() {
    local service=$1
    echo "Building $service..."
    cd $service
    mvn clean package -DskipTests
    if [ $? -eq 0 ]; then
        echo "✓ $service built successfully"
    else
        echo "✗ Failed to build $service"
        exit 1
    fi
    cd ..
}

# Build all services
build_service "authentication-service"
build_service "user-service"
build_service "workout-service"
build_service "nutrition-service"
build_service "recommendation-service"

echo "All services built successfully!"
echo "Run 'docker-compose up --build' to start all services"
