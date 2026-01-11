package com.example.weatherapplication

import com.example.weatherapplication.data.di.DataDiModule
import com.example.weatherapplication.domain.di.DomainDiModule
import com.example.weatherapplication.persentation.di.PersentationDiModule

val CombinedAppModules = listOf(
    DataDiModule,
    DomainDiModule,
    PersentationDiModule
)