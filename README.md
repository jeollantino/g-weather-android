# GWeather - Android Weather App

A modern Android weather application built with Kotlin, following MVVM architecture and using the latest Android development tools.

## 🌟 Features

- **Authentication**: Local registration and sign-in with secure credential storage
- **Current Weather**: Real-time weather information with detailed metrics
- **Weather History**: Persistent storage of weather data with timestamped entries
- **Device Location**: Automatic location detection with manual city entry fallback
- **Weather Icon Logic**: Shows nighttime weather icon when local time ≥ 18:00
- **Secure API Key**: API key stored in local.properties (not committed to version control)
- **Modern UI**: Beautiful Material Design 3 interface with Jetpack Compose
- **MVVM Architecture**: Clean separation of concerns with data, domain, and presentation layers
- **Comprehensive Testing**: Unit tests for all layers of the application

## 🏗️ Architecture

The app follows **MVVM Architecture** with the following folder structure:

```
g-weather-android
┣ data/
┃ ┣ api/           (OpenWeather API service)
┃ ┣ repository/    (Repository implementations)
┃ ┗ local/         (Room database & entities)
┣ domain/          (Use cases & repository interfaces)
┣ ui/
┃ ┣ screens/       (Compose screens)
┃ ┣ components/    (Reusable UI components)
┃ ┗ theme/         (Material Design theme)
┣ di/              (Hilt dependency injection)
┣ model/           (Data models)
┗ util/            (Utility classes)
```

### Architecture Flow
- **UI** → Composables and ViewModels
- **ViewModel** → Exposes state via StateFlow
- **Repository** → Handles API and Room data
- **Data sources** → Retrofit + Room DAO

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Local Database**: Room
- **Asynchronous**: Coroutines + Flow
- **Testing**: JUnit + MockK + Turbine
- **Security**: AndroidX Security Crypto

## 📱 App Flow

### 1. Authentication
- **Registration**: Create account with username, email, and password
- **Sign-In**: Login with username and password
- **Sign-Out**: Logout and return to authentication screen

### 2. Main App (Two Tabs)

#### Tab 1: Current Weather
- Shows current weather conditions for device location
- Displays temperature, weather condition, sunrise/sunset times
- Shows nighttime weather icon when local time ≥ 18:00
- Includes pull-to-refresh functionality
- Sign-out button in top bar

#### Tab 2: Weather History
- Lists all previously fetched weather data
- Shows timestamped entries with city, temperature, and conditions
- Data persists across app restarts
- Sign-out button in top bar

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.8.0+
- OpenWeather API key

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/jeollantino/g-weather-android.git
   cd g-weather-android
   ```

2. **Get your OpenWeather API key**
   - Visit [OpenWeatherMap](https://openweathermap.org/api)
   - Sign up for a free account
   - Generate an API key

3. **Configure the API key**
   For security, the API key is not included in the source code. This project uses a template file to show you where to place your key.
   - In the project's root directory, you will find a file named local.properties.template.
   - Create a copy of this file and rename it to local.properties.
   - Open the new `local.properties` file and replace the placeholder with your actual OpenWeather API key.
    
   ```properties
   # In local.properties
   OPENWEATHER_API_KEY=your_api_key_here
   ```
   - **Note**: `local.properties` is in `.gitignore` and will not be committed to version control

4. **Build and run**
   - Open the project in Android Studio.
   - Click the 'Run' button to build and install the app on an emulator or a physical device.

## 🔒 Security

### API Key Protection
- **Local Storage**: API key is stored in `local.properties` which is excluded from version control
- **BuildConfig**: API key is accessed via BuildConfig at compile time
- **ProGuard Rules**: Sensitive data is obfuscated in release builds
- **No Hardcoded Secrets**: All sensitive information is externalized
- **HTTPS Only**: All API calls use secure connections

### Authentication Security
- **Password Hashing**: Passwords are hashed using SHA-256
- **Encrypted Storage**: User credentials stored securely
- **Local Authentication**: No external authentication dependencies

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test classes
./gradlew test --tests "com.jeollantino.gweather.data.repository.*"
./gradlew test --tests "com.jeollantino.gweather.domain.usecase.*"
./gradlew test --tests "com.jeollantino.gweather.ui.viewmodel.*"
```

### Test Coverage

- **Data Layer**: Repository implementation tests with mocked dependencies
- **Domain Layer**: Use case tests with mocked repository
- **Presentation Layer**: ViewModel tests with mocked use cases
- **Room DAO**: Database tests with in-memory database

## 📦 Dependencies

### Core Dependencies
- `androidx.core:core-ktx` - Android KTX extensions
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle components
- `androidx.activity:activity-compose` - Compose activity integration

### UI Dependencies
- `androidx.compose:compose-bom` - Compose BOM for version management
- `androidx.compose.ui:ui` - Compose UI toolkit
- `androidx.compose.material3:material3` - Material Design 3 components
- `androidx.navigation:navigation-compose` - Compose navigation

### Architecture Dependencies
- `com.google.dagger:hilt-android` - Dependency injection
- `androidx.hilt:hilt-navigation-compose` - Hilt navigation integration
- `androidx.lifecycle:lifecycle-viewmodel-compose` - Compose ViewModel integration

### Data Dependencies
- `com.squareup.retrofit2:retrofit` - HTTP client
- `com.squareup.retrofit2:converter-gson` - JSON serialization
- `com.squareup.okhttp3:okhttp` - HTTP client implementation
- `androidx.room:room-runtime` - Local database
- `androidx.room:room-ktx` - Room Kotlin extensions

### Security Dependencies
- `androidx.security:security-crypto` - Encrypted storage

### Testing Dependencies
- `junit:junit` - Unit testing framework
- `io.mockk:mockk` - Mocking library
- `app.cash.turbine:turbine` - Flow testing utilities
- `org.jetbrains.kotlinx:kotlinx-coroutines-test` - Coroutine testing

## 🔧 Configuration

### API Configuration
The app uses the OpenWeather API with the following endpoints:
- Current Weather: `https://api.openweathermap.org/data/2.5/weather`

## 🐛 Troubleshooting

### Common Issues

1. **API Key Not Working**
   - Verify your API key is correct
   - Check if you have API quota remaining
   - Ensure internet connectivity

2. **No Weather Data**
   - Check internet connection
   - Verify API key is set
   - Check OpenWeather API status
   - Grant location permissions or enter city manually

3. **Authentication Issues**
   - Ensure username/email is unique during registration
   - Check password requirements
   - Verify credentials are correct during sign-in

4. **Build Errors**
   - Clean and rebuild project
   - Check Android SDK version
   - Verify all dependencies are resolved

### Debug Mode
Enable debug logging by setting `BuildConfig.DEBUG = true`. This will show:
- HTTP request/response logs
- Database operations
- Cache hit/miss information


## 🙏 Acknowledgments

- [OpenWeatherMap](https://openweathermap.org/) for providing weather data API
- [Android Developers](https://developer.android.com/) for excellent documentation
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI toolkit

---

**Happy Weather Tracking! 🌤️**