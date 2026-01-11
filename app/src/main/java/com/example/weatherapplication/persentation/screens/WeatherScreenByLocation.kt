package com.example.weatherapplication.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*   // or .outlined / .filled depending on style

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.weatherapplication.data.apiResponseModel.WeatherApiResponse
import com.example.weatherapplication.data.common.ResultState
import com.example.weatherapplication.persentation.navigation.Routes
import com.example.weatherapplication.persentation.viewModel.WeatherViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreenByLocation(
    viewModel: WeatherViewModel = koinViewModel<WeatherViewModel>(),
    navController: NavController
) {

    val context = LocalContext.current
    val locationWeatherState by viewModel.getWeatherByLocationState.collectAsState()
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    var isGpsEnabled by remember { mutableStateOf(isLocationEnabled(context)) }

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


    // Scroll state for parallax effect
    val scrollState = rememberScrollState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val wasGpsEnabled = isGpsEnabled
                val nowGpsEnabled = isLocationEnabled(context)

                isGpsEnabled = nowGpsEnabled
                // ✅ GPS just turned ON → auto fetch
                if (!wasGpsEnabled && nowGpsEnabled) {
                    viewModel.resetLocationWeatherState()
                    getLocationAndFetchWeather(context, viewModel)
                }


            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
        // Parallax background elements
        ParallaxBackground(scrollState)

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header with animated icon
            WeatherHeader()

            Spacer(modifier = Modifier.height(32.dp))

            when {
                locationPermissionState.status.isGranted -> {
                    if (!isGpsEnabled) {
                        GPSDisabledSection(
                            onOpenSettings = {
                                openLocationSettings(context)
                                isGpsEnabled = isLocationEnabled(context)
                                if (isGpsEnabled) {
                                    getLocationAndFetchWeather(context, viewModel)
                                }
                            }
                        )
                    } else {
                        LocationWeatherContent(
                            weatherState = locationWeatherState,
                            onRetry = { getLocationAndFetchWeather(context, viewModel) },
                            navController = navController
                        )
                    }

                    LaunchedEffect(locationPermissionState.status, isGpsEnabled) {
                        if (locationPermissionState.status.isGranted && locationWeatherState is ResultState.Ideal) {
                            getLocationAndFetchWeather(context, viewModel)
                        }
                    }
                }

                locationPermissionState.status.shouldShowRationale -> {
                    PermissionRationaleSection(
                        onRequestPermission = { locationPermissionState.launchPermissionRequest() }
                    )
                }

                else -> {
                    PermissionRequestSection(
                        onRequestPermission = { locationPermissionState.launchPermissionRequest() }
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        // Animated weather icon
        val infiniteTransition = rememberInfiniteTransition()
        val translateY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -10f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(16.dp, CircleShape, spotColor = Color.White.copy(alpha = 0.3f))
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .offset(y = translateY.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Current Weather",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Based on your location",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LocationWeatherContent(
    weatherState: ResultState<WeatherApiResponse>,
    onRetry: () -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (weatherState) {
            is ResultState.Loading -> {
                WeatherLoadingAnimation()
            }

            is ResultState.Success -> {
                val weatherData = weatherState.data
                AnimatedWeatherCard(
                    weatherData = weatherData,
                    locationName = "Current Location"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GradientButton(
                        onClick = onRetry,
                        text = "Refresh",
                        icon = Icons.Default.Refresh,
                        modifier = Modifier.weight(1f) // Equal width
                    )

                    GradientButton(
                        onClick = { navController.navigate(Routes.HomeScreenRoutes) },
                        text = "Search City",
                        icon = Icons.Default.Search,
                        modifier = Modifier.weight(1f) // Equal width
                    )
                }

            }

            is ResultState.Error -> {
                WeatherErrorState(
                    errorMessage = weatherState.message,
                    onRetry = onRetry
                )
            }

            is ResultState.Ideal -> {
                InitialWeatherPrompt(onRetry = onRetry)
            }
        }
    }
}

@Composable
private fun AnimatedWeatherCard(weatherData: WeatherApiResponse, locationName: String) {
    val infiniteTransition = rememberInfiniteTransition()
    val cardElevation by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(cardElevation.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Location header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF718096)
                    )
                }

                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Location",
                    tint = Color(0xFF667EEA)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main weather info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${weatherData.main?.temp ?: 0}°",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        text = weatherData.weather?.get(0)?.description?.replaceFirstChar { it.uppercase() } ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF718096)
                    )
                }

                WeatherIcon(weatherData.weather?.get(0)?.main ?: "")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weather details grid
            WeatherDetailsGrid(weatherData)
        }
    }
}

@Composable
private fun WeatherIcon(weatherCondition: String) {
    // normalize
    val cond = weatherCondition.lowercase()

    val icon = when (cond) {
        "clear" -> Icons.Rounded.WbSunny
        "clouds" -> Icons.Rounded.Cloud
        "rain" -> Icons.Rounded.Grain
        "snow" -> Icons.Rounded.AcUnit
        "thunderstorm" -> Icons.Rounded.FlashOn
        else -> Icons.Rounded.WbSunny
    }

    val iconColor = when (cond) {
        "clear" -> Color(0xFFFFB347)
        "clouds" -> Color(0xFF90A4AE)
        "rain" -> Color(0xFF64B5F6)
        "snow" -> Color(0xFFE3F2FD)
        "thunderstorm" -> Color(0xFF9575CD)
        else -> Color(0xFF667EEA)
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .background(iconColor.copy(alpha = 0.2f), CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = weatherCondition,
            tint = iconColor,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
private fun WeatherDetailsGrid(weatherData: WeatherApiResponse) {
    // use Rounded icons for consistency (requires material-icons-extended)
    val details = listOf(
        WeatherDetail("Feels like", "${weatherData.main?.feels_like ?: "--"}°", Icons.Rounded.Thermostat),
        WeatherDetail("Humidity", "${weatherData.main?.humidity ?: "--"}%", Icons.Rounded.WaterDrop),
        WeatherDetail("Wind", "${weatherData.wind?.speed ?: "--"} m/s", Icons.Rounded.Air),
        WeatherDetail("Pressure", "${weatherData.main?.pressure ?: "--"} hPa", Icons.Rounded.Speed)
    )

    // Non-scrollable grid built from Rows; safe to use inside verticalScroll parent
    Column(modifier = Modifier.fillMaxWidth()) {
        details.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { detail ->
                    // Each item takes equal width
                    Box(modifier = Modifier.weight(1f)) {
                        WeatherDetailCard(detail)
                    }
                }
                // if row had only 1 item (odd count), add an empty box to keep spacing
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Composable
private fun WeatherDetailCard(detail: WeatherDetail) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7FAFC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = detail.icon,
                    contentDescription = detail.label,
                    tint = Color(0xFF667EEA),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = detail.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF718096)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = detail.value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )
        }
    }
}

@Composable
private fun WeatherLoadingAnimation() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(48.dp)
    ) {
        // Pulsing circle animation
        val infiniteTransition = rememberInfiniteTransition()
        val pulse by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(pulse)
                .background(Color.White.copy(alpha = 0.3f), CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Detecting your location...",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WeatherErrorState(errorMessage: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Error",
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Unable to Load Weather",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            onClick = onRetry,
            text = "Try Again",
            icon = Icons.Default.Refresh
        )
    }
}

@Composable
private fun InitialWeatherPrompt(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Get Location",
            tint = Color.White,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Discover Your Weather",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Get real-time weather information for your current location with just one tap",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        GradientButton(
            onClick = onRetry,
            text = "Get Current Weather",
            icon = Icons.Default.PlayArrow
        )
    }
}

@Composable
private fun GradientButton(
    onClick: () -> Unit,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ParallaxBackground(scrollState: ScrollState) {
    val parallaxOffset = scrollState.value * 0.5f

    Box(modifier = Modifier.fillMaxSize()) {
        // Animated circles in background
        Box(
            modifier = Modifier
                .offset(x = (-100 + parallaxOffset * 0.1f).dp, y = 100.dp)
                .size(200.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )

        Box(
            modifier = Modifier
                .offset(x = (300 - parallaxOffset * 0.2f).dp, y = 400.dp)
                .size(150.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
        )

        Box(
            modifier = Modifier
                .offset(x = (100 + parallaxOffset * 0.15f).dp, y = 600.dp)
                .size(180.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
        )
    }
}

// Data class for weather details
data class WeatherDetail(val label: String, val value: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

// Rest of your existing functions remain the same...
@Composable
private fun PermissionRequestSection(onRequestPermission: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Text(
            text = "Location Access Required",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "To show weather for your current location, we need access to your device's location.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Allow Location Access")
        }
    }
}

@Composable
private fun PermissionRationaleSection(onRequestPermission: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Text(
            text = "Permission Needed",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Location permission is required to get weather for your current location. Please grant the permission when prompted.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Grant Permission")
        }
    }
}

@Composable
private fun GPSDisabledSection(onOpenSettings: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "Location Services Disabled",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Please enable GPS/location services to get weather data for your current location.",
            textAlign = TextAlign.Center,
            color = Color.White.copy(alpha = 0.85f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onOpenSettings) {
            Text("Enable Location Services")
        }
    }
}

// Keep your existing helper functions (getLocationAndFetchWeather, isLocationEnabled, openLocationSettings)
// Helper function to check location status
fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false ||
            locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false
}

fun openLocationSettings(context: Context) {
    val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}
@SuppressLint("MissingPermission")
private fun getLocationAndFetchWeather(context: Context, viewModel: WeatherViewModel) {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    try {
        // First try last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.getWeatherByLocation(location.latitude, location.longitude)
                } else {
                    // Request a fresh location update if last known is null
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { freshLocation: Location? ->
                        if (freshLocation != null) {
                            viewModel.getWeatherByLocation(
                                freshLocation.latitude,
                                freshLocation.longitude
                            )
                        } else {
                            viewModel.setLocationError(
                                "Unable to get current location. Please ensure location services are enabled and try again."
                            )
                        }
                    }.addOnFailureListener { e ->
                        viewModel.setLocationError("Error getting location: ${e.message}")
                    }
                }
            }
            .addOnFailureListener { e ->
                viewModel.setLocationError("Error getting location: ${e.message}")
            }

    } catch (e: SecurityException) {
        viewModel.setLocationError("Location permission required")
    } catch (e: Exception) {
        viewModel.setLocationError("Error getting location: ${e.message}")
    }
}