package com.example.weatherapplication.persentation.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import com.example.weatherapplication.persentation.navigation.Routes
import com.example.weatherapplication.persentation.viewModel.WeatherViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HomeScreen(
    viewModel: WeatherViewModel = koinViewModel<WeatherViewModel>(),
    navController: NavController
) {
    // State management for search visibility
    var showSearchSection by remember { mutableStateOf(true) }
    var city by remember { mutableStateOf("") }


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
        // Background elements
        HomeBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Header
            WeatherAppHeader()

            Spacer(modifier = Modifier.height(48.dp))

            if (showSearchSection) {
                // Search Section
                SearchSection(
                    city = city,
                    onCityChange = { city = it },
                    onSearchClick = {
                        if (city.isNotBlank()) {
                            navController.navigate(Routes.WeatherByCityRoutes(city))
                        }
                    },
                    onLocationClick = {
                        navController.navigate(Routes.WeatherScreenByLocationRoutes)
                        showSearchSection = false
                    },
                    navController
                )
            } else {
                // Location Section
                LocationSection(
                    onSearchAgain = { showSearchSection = true },
                    onGetLocationWeather = {
                        navController.navigate(Routes.WeatherScreenByLocationRoutes)
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}

@Composable
private fun HomeBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Animated background circles
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

@Composable
private fun WeatherAppHeader() {
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
                .shadow(20.dp, CircleShape, spotColor = Color.White.copy(alpha = 0.3f))
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                .clip(CircleShape),
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
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Get accurate weather information worldwide",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchSection(
    city: String,
    onCityChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onLocationClick: () -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Row: Back Arrow + Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF2D3748), // Dark text for visibility
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { navController.navigate(Routes.WeatherScreenByLocationRoutes) }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Search City Weather",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Enhanced Search Input
                SearchTextField(
                    value = city,
                    onValueChange = onCityChange,
                    onSearchClick = onSearchClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Or Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE2E8F0)
                    )
                    Text(
                        text = "OR",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFF718096),
                        style = MaterialTheme.typography.labelMedium
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE2E8F0)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Location Button
                GradientButton(
                    onClick = onLocationClick,
                    text = "Use My Location",
                    icon = Icons.Default.LocationOn,
                    gradientColors = listOf(Color(0xFF4CA1AF), Color(0xFF2C3E50))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Popular Cities
                Text(
                    text = "Popular Cities:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF718096)
                )

                Spacer(modifier = Modifier.height(12.dp))


                val cities = listOf("London", "Paris", "Tokyo", "New York")

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp), // each chip at least 100dp
                    modifier = Modifier.height(150.dp), // adjust height if needed
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cities) { popularCity ->
                        Chip(
                            onClick = { onCityChange(popularCity) },
                            label = { Text(popularCity) },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = Color(0xFF667EEA).copy(alpha = 0.1f),
                                contentColor = Color.Black,
                                secondaryContentColor = Color.Gray,
                                iconColor = Color.Black
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter city name...", color = Color(0xFFA0AEC0)) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF667EEA))
            },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748)
            ),
            singleLine = true
        )

        // Search button inside text field
        if (value.isNotBlank()) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Search",
                tint = Color(0xFF667EEA),
                modifier = Modifier
                    .size(70.dp)
                    .clickable { onSearchClick() }
                    .padding(8.dp)
                    .align(Alignment.CenterEnd)
                    .background(Color(0xFF667EEA).copy(alpha = 0.1f), CircleShape)
                    .clip(CircleShape)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun LocationSection(
    onSearchAgain: () -> Unit,
    onGetLocationWeather: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Location Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF4CA1AF).copy(alpha = 0.2f), CircleShape)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFF4CA1AF),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Weather by Location",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Get real-time weather information based on your current location",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF718096),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GradientButton(
                        onClick = onSearchAgain,
                        text = "Search City",
                        icon = Icons.Default.Search,
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFF718096), Color(0xFF4A5568))
                    )

                    GradientButton(
                        onClick = onGetLocationWeather,
                        text = "Get Weather",
                        icon = Icons.Default.WbSunny,
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFF4CA1AF), Color(0xFF2C3E50))
                    )
                }
            }
        }
    }
}

@Composable
private fun GradientButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        enabled = enabled,
        border = if (!enabled) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (enabled) gradientColors else listOf(Color(0xFFA0AEC0), Color(0xFF718096))
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
