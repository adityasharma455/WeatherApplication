package com.example.weatherapplication.data.di


import com.example.weatherapplication.data.network.apiBuilder.ApiBuilder
import com.example.weatherapplication.data.network.apiService.ApiService
import com.example.weatherapplication.data.repoImplementation.RepoImplementation
import com.example.weatherapplication.domain.repo.Repo
import org.koin.dsl.module

// Network Module
val DataDiModule = module {
    single { ApiBuilder() }
    single<ApiService> { get<ApiBuilder>().retrofitObject() }
    single<Repo>{ RepoImplementation(apiService = get()) }
}