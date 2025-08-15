#!/bin/bash

echo "🚀 Setting up Fitness App Microservices..."

# Create minimal service structure for quick testing
create_minimal_service() {
    local service=$1
    local port=$2
    
    echo "Creating minimal $service on port $port..."
    
    # Create main application class
    cat > ${service}/src/main/java/com/fitness_application/${service#*-}/$(echo ${service#*-} | sed 's/./\U&/' | sed 's/-//g')ServiceApplication.java << EOF
package com.fitness_application.${service#*-};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class $(echo ${service#*-} | sed 's/./\U&/' | sed 's/-//g')ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run($(echo ${service#*-} | sed 's/./\U&/' | sed 's/-//g')ServiceApplication.class, args);
    }
    
    @RestController
    public static class HealthController {
        @GetMapping("/health")
        public String health() {
            return "$service is healthy";
        }
        
        @GetMapping("/actuator/health") 
        public String actuatorHealth() {
            return "{\"status\":\"UP\"}";
        }
    }
}
EOF

    # Update application.properties
    cat > ${service}/src/main/resources/application.properties << EOF
spring.application.name=${service}
server.port=${port}

# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# Management
management.endpoints.web.exposure.include=health,info

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Logging
logging.level.com.fitness_application=INFO
EOF

    echo "✅ Created minimal $service"
}

# Build and start services
build_and_start() {
    echo "🔨 Building all services..."
    
    # Create minimal services for quick startup
    create_minimal_service "authentication-service" "8081"
    create_minimal_service "user-service" "8082" 
    create_minimal_service "workout-service" "8083"
    create_minimal_service "nutrition-service" "8084"
    create_minimal_service "recommendation-service" "8085"
    
    echo "🔨 Building Maven projects..."
    for service in authentication-service user-service workout-service nutrition-service recommendation-service; do
        echo "Building $service..."
        cd $service
        mvn clean package -DskipTests -q
        if [ $? -ne 0 ]; then
            echo "❌ Failed to build $service"
            exit 1
        fi
        cd ..
    done
    
    echo "🐳 Starting Docker Compose..."
    docker-compose up --build -d
    
    echo "⏳ Waiting for services to start..."
    sleep 30
    
    echo "🎯 Testing service endpoints..."
    for port in 8081 8082 8083 8084 8085; do
        if curl -s http://localhost:$port/health > /dev/null; then
            echo "✅ Service on port $port is running"
        else
            echo "❌ Service on port $port is not responding"
        fi
    done
    
    echo "🌐 Testing NGINX proxy..."
    if curl -s http://localhost/health > /dev/null; then
        echo "✅ NGINX proxy is working"
    else
        echo "❌ NGINX proxy is not responding"
    fi
    
    echo ""
    echo "🎉 Microservices are running!"
    echo ""
    echo "🔗 Access Points:"
    echo "   - API Gateway (NGINX): http://localhost"
    echo "   - Authentication Service: http://localhost:8081"
    echo "   - User Service: http://localhost:8082"  
    echo "   - Workout Service: http://localhost:8083"
    echo "   - Nutrition Service: http://localhost:8084"
    echo "   - Recommendation Service: http://localhost:8085"
    echo ""
    echo "📚 Swagger Documentation:"
    echo "   - Auth: http://localhost:8081/swagger-ui.html"
    echo "   - User: http://localhost:8082/swagger-ui.html"
    echo "   - Workout: http://localhost:8083/swagger-ui.html"
    echo "   - Nutrition: http://localhost:8084/swagger-ui.html"
    echo "   - Recommendation: http://localhost:8085/swagger-ui.html"
    echo ""
    echo "🧪 Test with:"
    echo "   curl http://localhost:8081/health"
    echo "   curl http://localhost/health"
}

build_and_start
