package com.example.weatherapplication.domain.UserUseCase

import com.example.weatherapplication.data.apiResponseModel.WeatherApiResponse
import com.example.weatherapplication.data.common.ResultState
import com.example.weatherapplication.domain.repo.Repo
import kotlinx.coroutines.flow.Flow

class GetWeatherByCityUserUseCase (private val repo: Repo) {
     fun getWeatherByCity(city: String): Flow<ResultState<WeatherApiResponse>>{
        return repo.getCityWeather(city)
    }
}