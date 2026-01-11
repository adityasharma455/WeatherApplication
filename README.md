🌤️ WeatherApplication

A simple and clean Android Weather App using Jetpack Compose, Koin DI, and Clean Architecture.

This app shows real-time weather:

  * Automatically using current GPS location
  * On demand using city name search

It’s designed to demonstrate professional Android skills — perfect for your portfolio and applying to internships.

📌 Features
🌍 Current Location Weather

  * Detects if GPS/location service is enabled
  * If not, navigates user to location settings
  * Shows weather based on device’s current location

🔎 Weather by City Name

  * Simple input field to type any city
  * Click Search to fetch weather for that city

🧠 Architecture

  * Clean Architecture with separation of layers
  * MVVM pattern
  * Koin for Dependency Injection

📸 Screenshots
![CheckGPS](screenshots/CheckGPS.jpg)
![GPSPermission](screenshots/Permission.jpg)
![Home](screenshots/HomeScreen.jpg)
![WeatherByCityName](screenshots/WeatherByCityName.jpg)

## 🎥 Demo Video
[Watch Demo](demo/WeatherDemo.mp4)


🛠️ Tech Stack
  * Category ->	Technologies
  * Language ->	Kotlin
  * UI ->	Jetpack Compose
  * Architecture ->	Clean Architecture, MVVM
  * DI ->	Koin
  * Networking ->	Retrofit, OkHttp
  * Location ->	FusedLocationProvider
  * Async / State ->	Coroutines, StateFlow


💻 How It Works

📍 Location Flow
  * On opening app → checks GPS status
  * If OFF → button to open system location settings
  * After enabling → fetch and display weather

🔎 City Search
  * User enters city name
  * Press Search
  * Weather data loads and shows on screen


📦 How to Run

  1-Clone the repository
    git clone https://github.com/adityasharma455/WeatherApplication
  2-Open with Android Studio Arctic Fox or above
  3-Add your API key (OpenWeatherMap or Weather API) in local.properties or in your network module
  4-Run on an Android device or emulator


📈 Why This Project

This project shows:
✔ Clean & scalable architecture
✔ State management using StateFlow
✔ Use of Dependency Injection (Koin)
✔ Handling of runtime permissions & system services

This makes the app robust and easy to maintain — which is expected in real-world Android roles.

📍 Future Enhancements

✔ Add weather forecast for upcoming days
✔ Offline caching (Room database)
✔ Dark/Light mode toggle
✔ Unit & UI Tests

👤 Author

Aditya Sharma
🎓 3rd Year Computer Science Student
📱 Android Developer 🧠 Building practical Android projects for internships

🔗 GitHub: https://github.com/adityasharma455
