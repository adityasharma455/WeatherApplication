package com.example.weatherapplication.data.network.apiService

import com.example.weatherapplication.data.apiResponseModel.WeatherApiResponse
import com.example.weatherapplication.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {


    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        // Query for city name (e.g., "London")
        @Query("q") city: String = "INDIA",
        // Your API key from OpenWeatherMap
        @Query("appid") apiKey: String = BuildConfig.API_KEY,
        // Optional: Units of measurement. 'metric' for Celsius.
        @Query("units") units: String = "metric"
    ): WeatherApiResponse

    // BONUS: You can also get weather by geographic coordinates
    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = BuildConfig.API_KEY,
        @Query("units") units: String = "metric"
    ): WeatherApiResponse
}