# 🚀 Fitness Application - Quick Setup Guide

## 📋 Overview
This guide helps you set up the Fitness Application with proper environment variable configuration.

## ⚡ Quick Setup

### **Step 1: Copy Environment File**
1. Locate the `.env` file in your project root
2. Fill in your actual values (see sections below)
3. Save the file

### **Step 2: Set Environment Variables (Windows)**

#### **Option A: Using Command Prompt**
```cmd
# Navigate to project directory
cd "d:\OneDrive\Documents\SystemDesignCodes\FitnessApp\Fitness_Application\backend\FitnessApplication"

# Set essential variables
setx JWT_SECRET "your-super-secret-jwt-signing-key-here"
setx GEMINI_API_KEY "your-gemini-api-key"
setx MAIL_USERNAME "your-email@gmail.com"
setx MAIL_PASSWORD "your-gmail-app-password"
setx GOOGLE_CLIENT_ID "your-google-client-id"
setx GOOGLE_CLIENT_SECRET "your-google-client-secret"
setx GITHUB_CLIENT_ID "your-github-client-id"
setx GITHUB_CLIENT_SECRET "your-github-client-secret"

# Restart your terminal and IDE after setting variables
```

#### **Option B: Using PowerShell**
```powershell
# Set essential variables
[Environment]::SetEnvironmentVariable("JWT_SECRET", "your-super-secret-jwt-signing-key-here", "User")
[Environment]::SetEnvironmentVariable("GEMINI_API_KEY", "your-gemini-api-key", "User")
[Environment]::SetEnvironmentVariable("MAIL_USERNAME", "your-email@gmail.com", "User")
[Environment]::SetEnvironmentVariable("MAIL_PASSWORD", "your-gmail-app-password", "User")
```

### **Step 3: Get Required API Keys**

#### **🔑 JWT Secret**
Generate a secure JWT secret:
```bash
# Using OpenSSL (if available)
openssl rand -base64 64

# Or use any strong random string generator
# Minimum 256 bits (32 characters) recommended
```

#### **🤖 Google Gemini API Key**
1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy the generated key

#### **📧 Gmail App Password**
1. Enable 2-Factor Authentication on your Google account
2. Go to [Google Account Security](https://myaccount.google.com/security)
3. Click "2-Step Verification" → "App passwords"
4. Select "Mail" and generate password
5. Copy the 16-character password

#### **🔐 Google OAuth2 Credentials**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create/select a project
3. Enable Google+ API
4. Create OAuth 2.0 Client ID credentials
5. Set redirect URI: `http://localhost:8080/login/oauth2/code/google`

#### **🐙 GitHub OAuth2 Credentials**
1. Go to [GitHub Settings](https://github.com/settings/applications/new)
2. Create new OAuth App
3. Set callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Note Client ID and Secret

### **Step 4: Start Application**
```cmd
# Navigate to project directory
cd "d:\OneDrive\Documents\SystemDesignCodes\FitnessApp\Fitness_Application\backend\FitnessApplication"

# Start the application
mvnw spring-boot:run

# Or with specific profile
mvnw spring-boot:run -Dspring.profiles.active=dev
```

### **Step 5: Test Your Setup**
- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **Health Check**: http://localhost:8080/actuator/health

## 📁 Project Structure After Cleanup

```
backend/FitnessApplication/
├── .env                           # ✅ Environment variables file
├── .gitignore                     # ✅ Updated with .env exclusion
├── src/main/resources/
│   ├── application.yml            # ✅ Main YAML configuration
│   ├── application-dev.yml        # ✅ Development environment
│   ├── application-prod.yml       # ✅ Production environment
│   ├── application-test.yml       # ✅ Test environment
│   ├── application-staging.yml    # ✅ Staging environment
│   └── application-docker.yml     # ✅ Docker environment
├── SECURITY-SETUP.md             # ✅ Security documentation
└── YAML-CONFIGURATION-GUIDE.md   # ✅ YAML configuration guide
```

## 🔒 Security Checklist

- ✅ All sensitive data uses environment variables
- ✅ `.env` file added to `.gitignore`
- ✅ Strong JWT secret configured
- ✅ OAuth2 credentials externalized
- ✅ Database credentials secured
- ✅ Production-ready error handling

## 🎯 Environment Profiles

### **Development (default)**
- H2 in-memory database
- Debug logging enabled
- H2 console accessible
- Swagger UI enabled

### **Production**
- PostgreSQL database
- Minimal logging
- Enhanced security
- Error details hidden

### **Test**
- Clean database per test
- Mocked external services
- Fast execution

## 🚨 Troubleshooting

### **Environment Variables Not Loading**
```cmd
# Check if variables are set (Windows)
echo %JWT_SECRET%
echo %GEMINI_API_KEY%

# PowerShell
$env:JWT_SECRET
$env:GEMINI_API_KEY
```

### **Database Connection Issues**
- Verify PostgreSQL is running (for production)
- Check database URL and credentials
- Ensure database exists

### **OAuth2 Issues**
- Verify redirect URIs match exactly
- Check client IDs and secrets
- Ensure APIs are enabled in Google Cloud Console

### **Application Won't Start**
```cmd
# Check for port conflicts
netstat -an | findstr :8080

# View detailed logs
mvnw spring-boot:run --debug
```

## 📞 Support

If you encounter issues:
1. Check environment variables are set correctly
2. Restart IDE and terminal after setting variables
3. Verify API keys are valid
4. Check application logs for detailed error messages
5. Test with development profile first

## 🎉 Success!

Once everything is configured:
- Your application will start on port 8080
- Swagger UI will be available for API testing
- All sensitive data will be properly externalized
- You can switch between environments easily

Happy coding! 🚀
