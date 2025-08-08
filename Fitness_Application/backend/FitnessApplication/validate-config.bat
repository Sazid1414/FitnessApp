@echo off
echo ==============================================================================
echo FITNESS APPLICATION - CONFIGURATION VALIDATOR
echo ==============================================================================

echo.
echo Checking environment variables...

REM Check essential variables
echo.
echo --- Essential Variables ---
if defined JWT_SECRET (
    echo ✓ JWT_SECRET is set
) else (
    echo ✗ JWT_SECRET is NOT set
    echo   Run: setx JWT_SECRET "your-jwt-secret-key"
)

if defined GEMINI_API_KEY (
    echo ✓ GEMINI_API_KEY is set
) else (
    echo ✗ GEMINI_API_KEY is NOT set
    echo   Get from: https://makersuite.google.com/app/apikey
)

if defined GOOGLE_CLIENT_ID (
    echo ✓ GOOGLE_CLIENT_ID is set
) else (
    echo ✗ GOOGLE_CLIENT_ID is NOT set
    echo   Get from: https://console.developers.google.com/
)

if defined GOOGLE_CLIENT_SECRET (
    echo ✓ GOOGLE_CLIENT_SECRET is set
) else (
    echo ✗ GOOGLE_CLIENT_SECRET is NOT set
)

echo.
echo --- Optional Variables ---
if defined MAIL_USERNAME (
    echo ✓ MAIL_USERNAME is set
) else (
    echo - MAIL_USERNAME is not set (optional)
)

if defined MAIL_PASSWORD (
    echo ✓ MAIL_PASSWORD is set
) else (
    echo - MAIL_PASSWORD is not set (optional)
)

echo.
echo --- Configuration Files Check ---
if exist "src\main\resources\application.yml" (
    echo ✓ Main application.yml exists
) else (
    echo ✗ Main application.yml is missing
)

if exist ".env" (
    echo ✓ .env file exists
) else (
    echo ✗ .env file is missing
)

if exist ".gitignore" (
    findstr /C:".env" .gitignore >nul
    if %errorlevel% == 0 (
        echo ✓ .env is in .gitignore
    ) else (
        echo ✗ .env is NOT in .gitignore
    )
) else (
    echo ✗ .gitignore file is missing
)

echo.
echo ==============================================================================
echo CONFIGURATION CHECK COMPLETE
echo ==============================================================================

if not defined JWT_SECRET (
    echo.
    echo ⚠ WARNING: Essential environment variables are missing
    echo Please set them before running the application:
    echo.
    echo setx JWT_SECRET "your-jwt-secret-key"
    echo setx GEMINI_API_KEY "your-gemini-api-key"
    echo setx GOOGLE_CLIENT_ID "your-google-client-id"
    echo setx GOOGLE_CLIENT_SECRET "your-google-client-secret"
    echo.
    echo Then restart your terminal and IDE.
) else (
    echo.
    echo ✅ Configuration looks good! You can start the application:
    echo mvnw spring-boot:run
)

echo.
pause
