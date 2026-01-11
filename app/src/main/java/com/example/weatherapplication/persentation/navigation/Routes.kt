package com.example.weatherapplication.persentation.navigation

import kotlinx.serialization.Serializable


sealed class SubNavigation{
    @Serializable
    object HomeScreenRoutes : SubNavigation()
}

sealed class Routes{
        @Serializable
        object HomeScreenRoutes

        @Serializable
        data class WeatherByCityRoutes (
            val city: String
        )

    @Serializable
    object  WeatherScreenByLocationRoutes

}