package com.example.weatherapplication.domain.UserUseCase

import com.example.weatherapplication.domain.repo.Repo

class GetWeatherByLocationUserUseCase(private val repo: Repo) {
   suspend fun getWeatherByLocation(lat: Double, lon: Double) = repo.getWeatherByLocation(lat = lat, lon = lon )
}