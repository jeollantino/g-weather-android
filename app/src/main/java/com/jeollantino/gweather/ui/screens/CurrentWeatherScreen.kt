package com.jeollantino.gweather.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeollantino.gweather.R
import com.jeollantino.gweather.domain.model.WeatherInfo
import com.jeollantino.gweather.ui.components.FooterTime
import com.jeollantino.gweather.ui.components.WeatherCard
import com.jeollantino.gweather.ui.components.WeatherGradientBackground
import com.jeollantino.gweather.ui.components.WeatherHeader
import com.jeollantino.gweather.ui.viewmodel.AuthViewModel
import com.jeollantino.gweather.ui.viewmodel.CurrentWeatherUiState
import com.jeollantino.gweather.ui.viewmodel.CurrentWeatherViewModel
import com.jeollantino.gweather.util.Constants
import com.jeollantino.gweather.util.LocationManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun CurrentWeatherScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onSignOut: () -> Unit,
    viewModel: CurrentWeatherViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }

    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionRequested by remember { mutableStateOf(false) }

    // Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            viewModel.loadWeatherByDeviceLocation()
        } else {
            showPermissionDialog = true
        }
    }

    // Request location permissions on initial composition
    LaunchedEffect(Unit) {
        if (!permissionRequested) {
            permissionRequested = true
            if (locationManager.hasLocationPermission()) {
                viewModel.loadWeatherByDeviceLocation()
            } else {
                locationPermissionLauncher.launch(locationManager.getRequiredPermissions())
            }
        }
    }

    // Permission denied dialog
    if (showPermissionDialog) {
        PermissionDeniedDialog(
            onDismiss = { showPermissionDialog = false },
            onRetry = {
                showPermissionDialog = false
                locationPermissionLauncher.launch(locationManager.getRequiredPermissions())
            }
        )
    }

    CurrentWeatherContent(
        uiState = uiState,
        username = authState.currentUser?.username,
        onSignOut = onSignOut,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.padding(contentPadding)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherContent(
    uiState: CurrentWeatherUiState,
    username: String?,
    onSignOut: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    WeatherGradientBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        TextButton(onClick = onSignOut) {
                            Text(stringResource(R.string.sign_out))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    // Error state
                    uiState.error != null && uiState.weatherInfo == null -> {
                        ErrorStateContent(
                            error = uiState.error,
                            onRetry = onRefresh
                        )
                    }
                    // Success state with weather data
                    uiState.weatherInfo != null -> {
                        SuccessStateContent(
                            weatherInfo = uiState.weatherInfo,
                            username = username,
                            onRetry = onRefresh
                        )
                    }
                    // Empty state (shown during initial permission setup and when no data)
                    else -> {
                        EmptyStateContent(onRefresh = onRefresh)
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessStateContent(
    weatherInfo: WeatherInfo,
    username: String?,
    onRetry: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Greeting Header
        if (username != null) {
            item {
                val context = LocalContext.current
                Text(
                    text = getGreeting(context, username, weatherInfo.timezoneOffset),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    textAlign = TextAlign.Start
                )
            }
        }

        // Weather Header
        item {
            WeatherHeader(
                location = "${weatherInfo.cityName}, ${weatherInfo.country}",
                modifier = Modifier
                    .semantics { testTag = "weather_header" },
            )
        }

        // Main Weather Card
        item {
            WeatherCard(
                temperature = "${weatherInfo.temperature.toInt()}${Constants.Units.DEGREE_SYMBOL}",
                unit = Constants.Units.CELSIUS,
                sunriseTime = formatTime(weatherInfo.sunrise, weatherInfo.timezoneOffset),
                sunsetTime = formatTime(weatherInfo.sunset, weatherInfo.timezoneOffset),
                icon = weatherInfo.icon,
                description = weatherInfo.description,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { testTag = "weather_card" }
            )
        }

        // Additional Weather Details
        item {
            WeatherDetailsCard(weatherInfo = weatherInfo)
        }

        // Current time
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.last_fetched_on),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
                FooterTime(
                    time = formatCurrentTime(weatherInfo.timezoneOffset),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun WeatherDetailsCard(weatherInfo: WeatherInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2947).copy(alpha = 0.6f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = weatherInfo.description.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            val context = LocalContext.current
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(label = stringResource(R.string.feels_like), value = "${weatherInfo.feelsLike.toInt()}${Constants.Units.DEGREE_SYMBOL}")
                DetailItem(label = stringResource(R.string.humidity), value = "${weatherInfo.humidity}${Constants.Units.PERCENT}")
                DetailItem(label = stringResource(R.string.wind), value = "${weatherInfo.windSpeed} ${Constants.Units.METERS_PER_SECOND}")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(label = stringResource(R.string.min_temp), value = "${weatherInfo.minTemp.toInt()}${Constants.Units.DEGREE_SYMBOL}")
                DetailItem(label = stringResource(R.string.max_temp), value = "${weatherInfo.maxTemp.toInt()}${Constants.Units.DEGREE_SYMBOL}")
                DetailItem(label = stringResource(R.string.pressure), value = "${weatherInfo.pressure} ${Constants.Units.HECTOPASCAL}")
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}

@Composable
private fun ErrorStateContent(
    error: String,
    onRetry: () -> Unit
) {
    var showError by remember { mutableStateOf(true) }

    if (showError) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A2947).copy(alpha = 0.6f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { showError = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close),
                                tint = Color.White
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.error),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFFFF6B6B)
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = {
                                showError = false
                                onRetry()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF2D0B57)
                            )
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateContent(onRefresh: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.no_weather_data),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Button(onClick = onRefresh) {
                Text(stringResource(R.string.load_weather))
            }
        }
    }
}

private fun formatTime(timestamp: Long, timezoneOffset: Int): String {
    val date = Date((timestamp + timezoneOffset) * 1000)
    val format = SimpleDateFormat(Constants.DateFormats.TIME_FORMAT_12H, Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone(Constants.TimeZone.UTC)
    return format.format(date)
}

private fun formatCurrentTime(timezoneOffset: Int): String {
    val currentTime = System.currentTimeMillis() / 1000 + timezoneOffset
    val date = Date(currentTime * 1000)
    val format = SimpleDateFormat(Constants.DateFormats.TIME_FORMAT_12H, Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone(Constants.TimeZone.UTC)
    return format.format(date)
}

private fun getGreeting(context: android.content.Context, username: String, timezoneOffset: Int): String {
    // Calculate the current hour in the weather location's timezone
    val currentTime = System.currentTimeMillis() / 1000 + timezoneOffset
    val date = Date(currentTime * 1000)
    val calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.TimeZone.UTC))
    calendar.time = date
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 0..11 -> context.getString(com.jeollantino.gweather.R.string.good_morning, username)
        in 12..17 -> context.getString(com.jeollantino.gweather.R.string.good_afternoon, username)
        else -> context.getString(com.jeollantino.gweather.R.string.good_evening, username)
    }
}

@Composable
private fun PermissionDeniedDialog(
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A2947).copy(alpha = 0.95f)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = Color.White
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.location_permission_required),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.location_permission_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(stringResource(R.string.continue_without))
                    }

                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF2D0B57)
                        )
                    ) {
                        Text(stringResource(R.string.grant_permission))
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    name = "Success State - Portrait"
)
@Composable
fun CurrentWeatherScreenPreview() {
    MaterialTheme {
        CurrentWeatherContent(
            uiState = CurrentWeatherUiState(
                isLoading = false,
                weatherInfo = WeatherInfo(
                    id = 1,
                    cityName = "Singapore",
                    country = "SG",
                    temperature = 28.0,
                    feelsLike = 32.0,
                    minTemp = 26.0,
                    maxTemp = 30.0,
                    description = "partly cloudy",
                    icon = "02d",
                    humidity = 75,
                    pressure = 1013,
                    windSpeed = 3.5,
                    windDirection = 180,
                    visibility = 10000,
                    sunrise = 1704073800,
                    sunset = 1704117000,
                    timestamp = System.currentTimeMillis() / 1000,
                    latitude = 1.3521,
                    longitude = 103.8198,
                    timezoneOffset = 28800
                )
            ),
            username = "John",
            onSignOut = {},
            onRefresh = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    name = "Error State"
)
@Composable
fun CurrentWeatherScreenErrorPreview() {
    MaterialTheme {
        CurrentWeatherContent(
            uiState = CurrentWeatherUiState(
                isLoading = false,
                error = "Failed to fetch weather data. Please check your internet connection."
            ),
            username = "John",
            onSignOut = {},
            onRefresh = {}
        )
    }
}


@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 360,
    name = "Success State - Landscape"
)
@Composable
fun CurrentWeatherScreenLandscapePreview() {
    MaterialTheme {
        CurrentWeatherContent(
            uiState = CurrentWeatherUiState(
                isLoading = false,
                weatherInfo = WeatherInfo(
                    id = 1,
                    cityName = "London",
                    country = "GB",
                    temperature = 15.0,
                    feelsLike = 14.0,
                    minTemp = 13.0,
                    maxTemp = 17.0,
                    description = "light rain",
                    icon = "10d",
                    humidity = 80,
                    pressure = 1015,
                    windSpeed = 4.0,
                    windDirection = 270,
                    visibility = 8000,
                    sunrise = 1704095400,
                    sunset = 1704127200,
                    timestamp = System.currentTimeMillis() / 1000,
                    latitude = 51.5074,
                    longitude = -0.1278,
                    timezoneOffset = 0
                )
            ),
            username = "Jane",
            onSignOut = {},
            onRefresh = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    name = "Empty State"
)
@Composable
fun CurrentWeatherScreenEmptyPreview() {
    MaterialTheme {
        CurrentWeatherContent(
            uiState = CurrentWeatherUiState(),
            username = "John",
            onSignOut = {},
            onRefresh = {}
        )
    }
}
