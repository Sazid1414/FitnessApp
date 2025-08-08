# 🔐 Fitness Application - Security Configuration Guide

## 📋 Overview
This guide explains how to properly secure your Fitness Application by externalizing sensitive configuration values and using environment variables instead of hardcoded values.

## 🚨 Security Issues Fixed
- ✅ JWT secret key externalized
- ✅ Database credentials externalized  
- ✅ API keys externalized
- ✅ OAuth2 credentials externalized
- ✅ Environment-specific configurations

## 🔑 Required Environment Variables

### **Essential Variables (Required)**
| Variable | Description | Example/Format |
|----------|-------------|----------------|
| `JWT_SECRET` | JWT signing secret key | Base64 encoded string (min 256 bits) |
| `GEMINI_API_KEY` | Google Gemini API key | `AIza...` |
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID | `123456789-abc...googleusercontent.com` |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | `GOCSPX-...` |
| `GITHUB_CLIENT_ID` | GitHub OAuth2 client ID | `Iv1.abc123...` |
| `GITHUB_CLIENT_SECRET` | GitHub OAuth2 client secret | `ghp_...` |

### **Email Configuration (Optional)**
| Variable | Description | Example |
|----------|-------------|---------|
| `MAIL_USERNAME` | Gmail username | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Gmail app password | `abcd efgh ijkl mnop` |

### **Database Configuration (Production)**
| Variable | Description | Example |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/fitness_app` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `your-secure-password` |

### **Application Configuration (Optional)**
| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `SERVER_PORT` | Application server port | `8080` |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `http://localhost:3000,http://localhost:3001` |

## 🛠️ Setup Methods

### **Method 1: Automated Setup (Recommended)**

#### Windows PowerShell:
```powershell
# Run the setup script
.\setup-env.ps1
```

#### Windows Command Prompt:
```cmd
# Run the setup script
setup-env.bat
```

### **Method 2: Manual Setup**

#### Windows (Command Prompt):
```cmd
# JWT Configuration
setx JWT_SECRET "your-super-secret-jwt-key-here"
setx JWT_EXPIRATION "86400000"

# Google Gemini API
setx GEMINI_API_KEY "your-gemini-api-key"

# OAuth2 Configuration
setx GOOGLE_CLIENT_ID "your-google-client-id"
setx GOOGLE_CLIENT_SECRET "your-google-client-secret"
setx GITHUB_CLIENT_ID "your-github-client-id"
setx GITHUB_CLIENT_SECRET "your-github-client-secret"

# Email Configuration
setx MAIL_USERNAME "your-email@gmail.com"
setx MAIL_PASSWORD "your-gmail-app-password"

# Application Configuration
setx SPRING_PROFILES_ACTIVE "dev"
setx CORS_ALLOWED_ORIGINS "http://localhost:3000,http://localhost:3001"
```

#### Windows (PowerShell):
```powershell
# JWT Configuration
[Environment]::SetEnvironmentVariable("JWT_SECRET", "your-super-secret-jwt-key-here", "User")
[Environment]::SetEnvironmentVariable("JWT_EXPIRATION", "86400000", "User")

# Google Gemini API
[Environment]::SetEnvironmentVariable("GEMINI_API_KEY", "your-gemini-api-key", "User")

# OAuth2 Configuration
[Environment]::SetEnvironmentVariable("GOOGLE_CLIENT_ID", "your-google-client-id", "User")
[Environment]::SetEnvironmentVariable("GOOGLE_CLIENT_SECRET", "your-google-client-secret", "User")
[Environment]::SetEnvironmentVariable("GITHUB_CLIENT_ID", "your-github-client-id", "User")
[Environment]::SetEnvironmentVariable("GITHUB_CLIENT_SECRET", "your-github-client-secret", "User")
```

### **Method 3: IDE Configuration**

#### VS Code:
1. Open VS Code settings (`Ctrl+,`)
2. Search for "terminal.integrated.env.windows"
3. Add your environment variables

#### IntelliJ IDEA:
1. Go to Run → Edit Configurations
2. Add environment variables in the "Environment variables" field

## 🔐 How to Get API Keys

### **1. Google Gemini API Key**
1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy the generated key

### **2. Google OAuth2 Credentials**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Go to Credentials → Create Credentials → OAuth 2.0 Client ID
5. Set authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`

### **3. GitHub OAuth2 Credentials**
1. Go to GitHub Settings → Developer settings → OAuth Apps
2. Click "New OAuth App"
3. Set Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Note down Client ID and Client Secret

### **4. Gmail App Password**
1. Enable 2-Factor Authentication on your Google account
2. Go to Google Account settings → Security → App passwords
3. Generate an app password for "Mail"
4. Use this 16-character password (not your regular password)

## 📁 File Structure After Setup
```
backend/FitnessApplication/
├── src/main/resources/
│   ├── application.properties          # Main config with env variables
│   ├── application-dev.properties      # Development config
│   ├── application-prod.properties     # Production config  
│   └── application-test.properties     # Test config
├── .env.template                       # Environment template
├── setup-env.ps1                      # PowerShell setup script
├── setup-env.bat                      # Batch setup script
└── .gitignore.env                     # Additional gitignore rules
```

## ✅ Verification Steps

1. **Restart your IDE and terminal**
2. **Check environment variables:**
   ```cmd
   # Windows Command Prompt
   echo %JWT_SECRET%
   
   # Windows PowerShell
   $env:JWT_SECRET
   ```

3. **Run your application:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Check application logs** for successful configuration loading

5. **Test the endpoints** using Swagger UI: `http://localhost:8080/swagger-ui.html`

## 🔒 Security Best Practices

### ✅ Do:
- Use environment variables for all sensitive data
- Use different configurations for dev/test/prod environments
- Generate strong, unique JWT secrets (minimum 256 bits)
- Use app-specific passwords for Gmail
- Regularly rotate API keys and secrets
- Never commit `.env` files to version control

### ❌ Don't:
- Hardcode secrets in properties files
- Share API keys in chat, email, or documentation
- Use the same secrets across multiple environments
- Commit environment files to Git
- Use weak or predictable secrets

## 🚨 Emergency Response
If you accidentally committed secrets:
1. **Immediately revoke/regenerate** all exposed credentials
2. **Remove secrets from Git history** using tools like BFG Repo-Cleaner
3. **Update all environments** with new credentials
4. **Review access logs** for unauthorized usage

## 📞 Support
If you encounter issues:
1. Check that all required environment variables are set
2. Restart your IDE and terminal after setting variables
3. Verify the application profile is correctly set
4. Check application logs for detailed error messages
