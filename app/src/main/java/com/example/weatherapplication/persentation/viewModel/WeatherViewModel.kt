package com.example.weatherapplication.persentation.viewModel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.apiResponseModel.WeatherApiResponse
import com.example.weatherapplication.data.common.ResultState
import com.example.weatherapplication.domain.UserUseCase.GetWeatherByCityUserUseCase
import com.example.weatherapplication.domain.UserUseCase.GetWeatherByLocationUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getWeatherByCityUserUseCase: GetWeatherByCityUserUseCase,
    private val getWeatherByLocationUserUseCase: GetWeatherByLocationUserUseCase
) : ViewModel() {

    private val _getWeatherByCityState = MutableStateFlow<ResultState<WeatherApiResponse>>(ResultState.Ideal)
    val getWeatherByCityState = _getWeatherByCityState.asStateFlow()

    private val _getWeatherByLocationState = MutableStateFlow<ResultState<WeatherApiResponse>>(ResultState.Ideal)
    val getWeatherByLocationState = _getWeatherByLocationState.asStateFlow()

    fun getWeatherByCity(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getWeatherByCityUserUseCase.getWeatherByCity(city).collect { resultState ->
                when (resultState) {
                    is ResultState.Loading -> {
                        _getWeatherByCityState.value = ResultState.Loading
                    }
                    is ResultState.Error -> {
                        _getWeatherByCityState.value = ResultState.Error(resultState.message)
                    }
                    is ResultState.Ideal -> {
                        _getWeatherByCityState.value = ResultState.Ideal
                    }
                    is ResultState.Success -> {
                        _getWeatherByCityState.value = ResultState.Success(resultState.data)
                    }
                }
            }
        }
    }

    fun getWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            getWeatherByLocationUserUseCase.getWeatherByLocation(lat, lon).collect { resultState ->
                when (resultState) {
                    is ResultState.Loading -> {
                        _getWeatherByLocationState.value = ResultState.Loading
                    }
                    is ResultState.Error -> {
                        _getWeatherByLocationState.value = ResultState.Error(resultState.message)
                    }
                    is ResultState.Ideal -> {
                        _getWeatherByLocationState.value = ResultState.Ideal
                    }
                    is ResultState.Success -> {
                        _getWeatherByLocationState.value = ResultState.Success(resultState.data)
                    }
                }
            }
        }
    }

    fun resetCityWeatherState() {
        _getWeatherByCityState.value = ResultState.Ideal
    }

    fun resetLocationWeatherState() {
        _getWeatherByLocationState.value = ResultState.Ideal
    }
    // Add this to your WeatherViewModel class
    fun setLocationError(message: String) {
        _getWeatherByLocationState.value = ResultState.Error(message)
    }
}

