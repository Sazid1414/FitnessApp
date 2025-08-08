# =============================================================================
# FITNESS APPLICATION YAML CONFIGURATION GUIDE
# =============================================================================

## 📋 Overview
This guide explains the YAML configuration files for your Fitness Application and how to use them effectively.

## 📁 Configuration Files Structure

```
src/main/resources/
├── application.yml              # Main configuration file
├── application-dev.yml          # Development environment
├── application-prod.yml         # Production environment
├── application-test.yml         # Test environment
├── application-staging.yml      # Staging environment
└── application-docker.yml       # Docker environment
```

## 🔧 Configuration Files Explained

### **1. application.yml (Main Configuration)**
- Contains default configuration that applies to all environments
- Uses environment variables with default fallback values
- Includes all shared configurations (JWT, AI, Mail, OAuth2, etc.)

### **2. application-dev.yml (Development)**
- **Database**: H2 in-memory database with console enabled
- **Logging**: Verbose logging with DEBUG level
- **JWT**: Shorter expiration (1 hour) for testing
- **CORS**: Permissive settings for local development
- **Swagger**: Fully enabled with all features

### **3. application-prod.yml (Production)**
- **Database**: PostgreSQL with connection pooling
- **Logging**: Minimal logging, file-based with rotation
- **JWT**: Standard 24-hour expiration
- **CORS**: Restrictive, environment-specific origins
- **Security**: Enhanced security headers
- **Swagger**: Disabled by default (can be enabled via env vars)

### **4. application-test.yml (Testing)**
- **Database**: H2 in-memory, recreated for each test
- **Logging**: Minimal console output
- **JWT**: Very short expiration (1 minute)
- **Services**: Mocked external services
- **Server**: Random port assignment

### **5. application-staging.yml (Staging)**
- **Database**: Separate PostgreSQL instance for staging
- **Logging**: Balanced verbosity for debugging
- **JWT**: 2-hour expiration
- **Swagger**: Enabled for API testing
- **CORS**: Moderately permissive

### **6. application-docker.yml (Docker)**
- **Database**: PostgreSQL container connection
- **Server**: Binds to 0.0.0.0 for container access
- **Logging**: Container-friendly console output

## 🚀 How to Use Different Environments

### **Method 1: Environment Variable**
```bash
# Windows Command Prompt
set SPRING_PROFILES_ACTIVE=dev
mvnw spring-boot:run

# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="dev"
./mvnw spring-boot:run

# Set permanently
setx SPRING_PROFILES_ACTIVE "dev"
```

### **Method 2: Command Line Argument**
```bash
# Development
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Production
./mvnw spring-boot:run -Dspring.profiles.active=prod

# Multiple profiles
./mvnw spring-boot:run -Dspring.profiles.active=prod,monitoring
```

### **Method 3: IDE Configuration**
- **IntelliJ IDEA**: Run Configuration → Environment Variables → `SPRING_PROFILES_ACTIVE=dev`
- **VS Code**: Launch configuration → env → `"SPRING_PROFILES_ACTIVE": "dev"`

### **Method 4: JAR Execution**
```bash
java -jar -Dspring.profiles.active=prod fitness-application.jar
```

## 🔍 Configuration Priority Order

Spring Boot loads configurations in this order (higher number = higher priority):

1. `application.yml` (default)
2. `application-{profile}.yml` (profile-specific)
3. Environment variables
4. Command line arguments
5. System properties

## 🎯 Environment-Specific Features

### **Development Features**
- ✅ H2 Console at `/h2-console`
- ✅ Detailed SQL logging
- ✅ Hot reload support
- ✅ Permissive CORS
- ✅ Full Swagger documentation

### **Production Features**
- ✅ PostgreSQL with connection pooling
- ✅ Security headers
- ✅ Log file rotation
- ✅ Actuator endpoints (limited)
- ✅ Error details hidden

### **Test Features**
- ✅ Clean database state per test
- ✅ Mocked external services
- ✅ Fast JWT expiration
- ✅ Random port allocation

## 🔧 Environment Variables Required

### **All Environments**
```yaml
JWT_SECRET: your-jwt-secret-key
GEMINI_API_KEY: your-gemini-api-key
GOOGLE_CLIENT_ID: your-google-client-id
GOOGLE_CLIENT_SECRET: your-google-client-secret
GITHUB_CLIENT_ID: your-github-client-id
GITHUB_CLIENT_SECRET: your-github-client-secret
```

### **Production Only**
```yaml
DATABASE_URL: jdbc:postgresql://localhost:5432/fitness_app
DATABASE_USERNAME: postgres
DATABASE_PASSWORD: your-secure-password
CORS_ALLOWED_ORIGINS: https://your-domain.com
```

### **Optional**
```yaml
MAIL_USERNAME: your-email@gmail.com
MAIL_PASSWORD: your-gmail-app-password
SERVER_PORT: 8080
LOG_LEVEL: INFO
```

## 📊 Configuration Examples

### **Development Setup**
```bash
# Set development profile
setx SPRING_PROFILES_ACTIVE "dev"

# Start application
./mvnw spring-boot:run

# Access H2 Console
http://localhost:8080/h2-console

# Access Swagger UI
http://localhost:8080/swagger-ui.html
```

### **Production Setup**
```bash
# Set production environment variables
setx SPRING_PROFILES_ACTIVE "prod"
setx DATABASE_URL "jdbc:postgresql://localhost:5432/fitness_app"
setx DATABASE_USERNAME "fitness_user"
setx DATABASE_PASSWORD "secure_password"
setx CORS_ALLOWED_ORIGINS "https://myapp.com"

# Build and run
./mvnw clean package -DskipTests
java -jar target/fitness-application.jar
```

### **Docker Setup**
```bash
# Build application
./mvnw clean package -DskipTests

# Run with Docker profile
docker run -e SPRING_PROFILES_ACTIVE=docker \
  -e DATABASE_PASSWORD=docker_password \
  -p 8080:8080 fitness-app:latest
```

## 🔧 Customizing Configuration

### **Adding New Profiles**
Create `application-{profile}.yml`:
```yaml
# application-custom.yml
spring:
  profiles:
    active: custom

# Your custom configurations here
logging:
  level:
    com.fitness_application: TRACE
```

### **Profile-Specific Beans**
```java
@Component
@Profile("dev")
public class DevDataLoader {
    // Development-only component
}

@Component
@Profile("prod")
public class ProdEmailService {
    // Production-only component
}
```

## ⚠️ Important Notes

1. **Profile Activation**: Only one main profile should be active at a time
2. **Environment Variables**: Always take precedence over YAML values
3. **Security**: Never commit sensitive values to version control
4. **Testing**: Use `@ActiveProfiles("test")` in test classes
5. **Docker**: Use environment-specific profiles for containers

## 🔍 Troubleshooting

### **Profile Not Loading**
```bash
# Check active profiles
curl http://localhost:8080/actuator/env | grep profiles

# View all configuration sources
curl http://localhost:8080/actuator/configprops
```

### **Environment Variable Issues**
```bash
# Check environment variables (Windows)
echo %SPRING_PROFILES_ACTIVE%
echo %JWT_SECRET%

# Check in PowerShell
$env:SPRING_PROFILES_ACTIVE
$env:JWT_SECRET
```

### **Database Connection Issues**
- Check if PostgreSQL is running
- Verify connection URL and credentials
- Ensure database exists
- Check firewall settings

This YAML configuration provides a robust, environment-aware setup for your Fitness Application with proper separation of concerns and security best practices.
