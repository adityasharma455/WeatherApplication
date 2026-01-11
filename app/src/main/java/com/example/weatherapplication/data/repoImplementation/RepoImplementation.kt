package com.example.weatherapplication.data.repoImplementation


import android.util.Log
import com.example.weatherapplication.data.apiResponseModel.WeatherApiResponse
import com.example.weatherapplication.data.common.ResultState
import com.example.weatherapplication.data.network.apiService.ApiService
import com.example.weatherapplication.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class RepoImplementation(
    private val apiService: ApiService
) : Repo {

    override fun getCityWeather(city: String): Flow<ResultState<WeatherApiResponse>> = flow {
        emit(ResultState.Loading)
        try {
            // Make the API call
            val response = apiService.getCurrentWeather(city)

            // Log the response for debugging
            Log.d("RepoImplementation", "Weather data received: $response")

            // Emit success state with data
            emit(ResultState.Success(response))

        } catch (e: HttpException) {
            // Handle HTTP errors (4xx, 5xx)
            val errorMessage = when (e.code()) {
                401 -> "Unauthorized: Invalid API key"
                404 -> "City not found: $city"
                429 -> "Too many requests: Rate limit exceeded"
                500 -> "Server error: Please try again later"
                else -> "HTTP error ${e.code()}: ${e.message()}"
            }
            Log.e("RepoImplementation", "HTTP error: $errorMessage", e)
            emit(ResultState.Error(errorMessage))

        } catch (e: IOException) {
            // Handle network errors
            val errorMessage = "Network error: Please check your internet connection"
            Log.e("RepoImplementation", "Network error", e)
            emit(ResultState.Error(errorMessage))

        } catch (e: Exception) {
            // Handle any other unexpected errors
            val errorMessage = "Unexpected error: ${e.message ?: "Unknown error occurred"}"
            Log.e("RepoImplementation", "Unexpected error", e)
            emit(ResultState.Error(errorMessage))
        }
    }

    override suspend fun getWeatherByLocation(
        lat: Double,
        lon: Double
    ): Flow<ResultState<WeatherApiResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val response = apiService.getCurrentWeatherByLocation(lat, lon, "2e0b62ce4566b4a69432fbec2d65839c")
            Log.d("RepoImplementation", "Location weather data received: $response")
            emit(ResultState.Success(response))

        } catch (e: HttpException) {
            val errorMessage = "HTTP error ${e.code()}: ${e.message()}"
            Log.e("RepoImplementation", "HTTP error in location API", e)
            emit(ResultState.Error(errorMessage))

        } catch (e: IOException) {
            val errorMessage = "Network error: Please check your internet connection"
            Log.e("RepoImplementation", "Network error in location API", e)
            emit(ResultState.Error(errorMessage))

        } catch (e: Exception) {
            val errorMessage = "Unexpected error: ${e.message ?: "Unknown error occurred"}"
            Log.e("RepoImplementation", "Unexpected error in location API", e)
            emit(ResultState.Error(errorMessage))
        }
    }

    }






