package com.example.weatherapplication.domain.repo

import com.example.weatherapplication.data.apiResponseModel.WeatherApiResponse
import com.example.weatherapplication.data.common.ResultState
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun getCityWeather(city: String): Flow<ResultState<WeatherApiResponse>>
    suspend fun getWeatherByLocation(lat: Double, lon: Double): Flow<ResultState<WeatherApiResponse>>
}