package com.example.weatherapplication.persentation.navigation


import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

import com.example.weatherapplication.persentation.screens.HomeScreen
import com.example.weatherapplication.presentation.screens.WeatherByCityScreen
import com.example.weatherapplication.presentation.ui.WeatherScreenByLocation

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    println("DEBUG: AppNavigation - NavController created")

    NavHost(
        navController = navController,
        startDestination = SubNavigation.HomeScreenRoutes

    ) {
        navigation<SubNavigation.HomeScreenRoutes>(
            startDestination = Routes.WeatherScreenByLocationRoutes
        ){

            composable<Routes.HomeScreenRoutes> {
                HomeScreen(navController = navController)
            }
            composable<Routes.WeatherByCityRoutes> {
                val data = it.toRoute<Routes.WeatherByCityRoutes>()
                WeatherByCityScreen(
                    navController= navController,
                    city = data.city)
            }

            composable<Routes.WeatherScreenByLocationRoutes> {
                WeatherScreenByLocation(navController = navController)

            }
        }
    }



}

