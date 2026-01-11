package com.example.weatherapplication.data.network.apiBuilder

import com.example.weatherapplication.data.network.apiService.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiBuilder {

    fun retrofitObject(): ApiService {

        // 1. Create the Logging Interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 2. Create an OkHttpClient and add the interceptor & timeouts
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Add this line
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        // 3. Build Retrofit with this client
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}