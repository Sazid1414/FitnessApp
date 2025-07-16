# 🏋️ Fitness Application

A comprehensive fitness tracking application built with Spring Boot (Java) backend and React (TypeScript) frontend.

## 📋 Features

- **User Authentication**: Registration, login with JWT tokens + OAuth2 (Google & GitHub)
- **Workout Tracking**: Create, manage, and track workout sessions
- **AI Integration**: Get personalized workout and nutrition recommendations
- **Dashboard**: View progress, statistics, and recent activities
- **Goal Setting**: Set and track fitness goals
- **OAuth2 Login**: Seamless login with Google and GitHub
- **Responsive Design**: Modern UI with Tailwind CSS

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.5.3** with Java 21
- **Spring Security** with JWT authentication
- **Spring Data JPA** with H2/PostgreSQL
- **Spring Web** for REST APIs
- **Lombok** for boilerplate reduction
- **ModelMapper** for DTO mapping
- **OpenAPI/Swagger** for API documentation

### Frontend
- **React 18** with TypeScript
- **React Router** for navigation
- **Axios** for HTTP requests
- **React Hook Form** for form handling
- **Tailwind CSS** for styling
- **Context API** for state management

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Node.js 18+
- Maven 3.6+

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend/FitnessApplication
   ```

2. **Configure OAuth2 (Optional but recommended)**:
   
   **Google OAuth2 Setup**:
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select existing one
   - Enable Google+ API
   - Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client IDs"
   - Application type: Web application
   - Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`
   - Copy Client ID and Client Secret

   **GitHub OAuth2 Setup**:
   - Go to GitHub Settings → Developer settings → OAuth Apps
   - Click "New OAuth App"
   - Application name: Fitness Application
   - Homepage URL: `http://localhost:3000`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
   - Copy Client ID and Client Secret

   **Set Environment Variables**:
   ```bash
   export GOOGLE_CLIENT_ID="your-google-client-id"
   export GOOGLE_CLIENT_SECRET="your-google-client-secret"
   export GITHUB_CLIENT_ID="your-github-client-id"
   export GITHUB_CLIENT_SECRET="your-github-client-secret"
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

3. API Documentation:
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console: `http://localhost:8080/h2-console`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

   The frontend will start on `http://localhost:3000`

## 📊 API Endpoints

### Authentication
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login
- `GET /api/v1/auth/me` - Get current user
- `PUT /api/v1/auth/profile` - Update profile
- `GET /api/v1/auth/oauth2/authorization/{provider}` - OAuth2 login endpoint

### Workouts
- `GET /api/v1/workouts` - Get user workouts
- `POST /api/v1/workouts` - Create workout
- `PUT /api/v1/workouts/{id}` - Update workout
- `DELETE /api/v1/workouts/{id}` - Delete workout
- `PUT /api/v1/workouts/{id}/complete` - Complete workout

### AI Assistant
- `POST /api/v1/ai/workout-recommendation` - Get workout recommendation
- `POST /api/v1/ai/nutrition-advice` - Get nutrition advice
- `POST /api/v1/ai/general-advice` - Get general fitness advice

## 🔧 Configuration

### Backend Configuration
Update `application.properties` for different environments:

```properties
# Database (PostgreSQL for production)
spring.datasource.url=jdbc:postgresql://localhost:5432/fitness_app
spring.datasource.username=your_username
spring.datasource.password=your_password

# AI Service
ai.gemini.api.key=your_gemini_api_key

# Mail Service
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### Frontend Configuration
Create `.env` file in frontend directory:

```env
REACT_APP_API_URL=http://localhost:8080/api/v1
```

## 📱 Features Overview

### User Management
- ✅ User registration with profile setup
- ✅ Secure login with JWT authentication
- ✅ Profile management
- ✅ Role-based access control

### Workout Management
- ✅ Create and manage workout sessions
- ✅ Track exercises, sets, reps, and duration
- ✅ Mark workouts as completed
- ✅ View workout history

### AI Integration
- ✅ Personalized workout recommendations
- ✅ Nutrition advice based on user profile
- ✅ General fitness guidance
- 🔄 Mock responses (integrate with actual AI service)

### Dashboard
- ✅ User statistics and progress
- ✅ Quick actions for common tasks
- ✅ Recent activity overview
- 🔄 Real-time data updates

## 🔮 Future Enhancements

- [ ] Nutrition logging and calorie tracking
- [ ] Goal setting and progress monitoring
- [ ] Social features and progress sharing
- [ ] Mobile app development
- [ ] Wearable device integration
- [ ] Advanced analytics and reporting
- [ ] Push notifications
- [ ] Meal planning and recipes

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the powerful frontend library
- Tailwind CSS for the utility-first CSS framework
- All contributors and testers
