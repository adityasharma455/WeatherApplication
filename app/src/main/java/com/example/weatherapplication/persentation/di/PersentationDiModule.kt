package com.example.weatherapplication.persentation.di

import com.example.weatherapplication.domain.UserUseCase.GetWeatherByCityUserUseCase
import com.example.weatherapplication.persentation.viewModel.WeatherViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val PersentationDiModule = module {
    viewModel<WeatherViewModel>{ WeatherViewModel(
        getWeatherByCityUserUseCase = get(),
        getWeatherByLocationUserUseCase = get()

    ) }
}