package com.example.weatherapplication.domain.di

import com.example.weatherapplication.domain.UserUseCase.GetWeatherByCityUserUseCase
import com.example.weatherapplication.domain.UserUseCase.GetWeatherByLocationUserUseCase
import org.koin.dsl.module

val DomainDiModule = module {

    factory { GetWeatherByCityUserUseCase(repo = get ()) }
    factory { GetWeatherByLocationUserUseCase(repo = get()) }
}