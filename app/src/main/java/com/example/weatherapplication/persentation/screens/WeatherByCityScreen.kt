package com.example.weatherapplication.presentation.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.weatherapplication.data.apiResponseModel.WeatherApiResponse
import com.example.weatherapplication.data.common.ResultState
import com.example.weatherapplication.persentation.navigation.Routes
import com.example.weatherapplication.persentation.viewModel.WeatherViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WeatherByCityScreen(
    navController: NavController,
    viewModel: WeatherViewModel = koinViewModel(),
    city: String
) {
    val weatherState by viewModel.getWeatherByCityState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getWeatherByCity(city)
    }

    // Background gradient animation
    val infiniteTransition = rememberInfiniteTransition()
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF667EEA),
        targetValue = Color(0xFF764BA2),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(animatedColor, Color(0xFF191919)),
                    startY = 0f,
                    endY = 1200f
                )
            )
    ) {
        HomeBackground() // optional animated background shapes

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeatherAppHeader()

            Spacer(modifier = Modifier.height(32.dp))

            when (val result = weatherState) {
                is ResultState.Ideal -> {
                    Text(
                        text = "Welcome to Weather App",
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        modifier = Modifier.clickable {
                            navController.navigate("home_screen")
                        }
                    )
                }

                is ResultState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading weather data...",
                        color = Color.White
                    )
                }

                is ResultState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "❌ Error",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = result.message,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Please check the city name and try again",
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is ResultState.Success -> {
                    WeatherCardStyled(
                        weather = result.data,
                        onBackClick = { navController.navigate("home_screen") },
                        navController
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherCardStyled(weather: WeatherApiResponse, onBackClick: () -> Unit, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Back button
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable { navController.navigate(Routes.HomeScreenRoutes) }
            )

            Text(
                text = "${weather.name ?: "Unknown City"}, ${weather.sys?.country ?: ""}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            weather.weather?.firstOrNull()?.icon?.let { iconCode ->
                val imageUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Weather icon",
                    modifier = Modifier.size(100.dp)
                )
            }

            Text(
                text = "${weather.main?.temp?.toInt() ?: "N/A"}°C",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            weather.weather?.firstOrNull()?.description?.let { description ->
                Text(
                    text = description.replaceFirstChar { it.uppercase() },
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem("Feels like", "${weather.main?.feels_like?.toInt() ?: "N/A"}°C")
                WeatherDetailItem("Humidity", "${weather.main?.humidity ?: "N/A"}%")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem("Wind", "${weather.wind?.speed ?: "N/A"} m/s")
                WeatherDetailItem("Pressure", "${weather.main?.pressure ?: "N/A"} hPa")
            }

            weather.visibility?.let { visibility ->
                WeatherDetailItem("Visibility", "${visibility / 1000} km")
            }
        }
    }
}

@Composable
fun WeatherDetailItem(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun WeatherAppHeader() {
    val infiniteTransition = rememberInfiniteTransition()
    val translateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Weather App",
                tint = Color.White,
                modifier = Modifier
                    .size(50.dp)
                    .offset(y = translateY.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Weather Forecast",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Get accurate weather information worldwide",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HomeBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset(x = (-100).dp, y = 100.dp)
                .size(200.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = 300.dp, y = 400.dp)
                .size(150.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = 100.dp, y = 600.dp)
                .size(180.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
        )
    }
}
