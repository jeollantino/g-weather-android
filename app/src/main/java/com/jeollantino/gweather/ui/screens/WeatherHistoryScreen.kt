package com.jeollantino.gweather.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeollantino.gweather.R
import com.jeollantino.gweather.model.WeatherHistoryUiState
import com.jeollantino.gweather.ui.components.WeatherGradientBackground
import com.jeollantino.gweather.ui.viewmodel.WeatherHistoryViewModel
import com.jeollantino.gweather.util.Constants
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherHistoryScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onSignOut: () -> Unit,
    viewModel: WeatherHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    WeatherHistoryContent(
        uiState = uiState,
        onSignOut = onSignOut,
        onRetry = { viewModel.loadWeatherHistory() },
        modifier = Modifier.padding(contentPadding)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHistoryContent(
    uiState: WeatherHistoryUiState,
    onSignOut: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showError by remember { mutableStateOf(true) }

    // Reset showError when error changes
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            showError = true
        }
    }

    WeatherGradientBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.weather_history)) },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(color = Color.White)
                                Text(
                                    text = stringResource(R.string.loading_weather_history),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else if (uiState.error != null && showError) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
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
                                        text = uiState.error,
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
                } else if (uiState.weatherHistory.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1A2947).copy(alpha = 0.6f)
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.no_weather_history),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = stringResource(R.string.weather_history_empty_message),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(uiState.weatherHistory) { weatherHistory ->
                        WeatherHistoryItem(weatherHistory = weatherHistory)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherHistoryItem(
    weatherHistory: com.jeollantino.gweather.model.WeatherHistory
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2947).copy(alpha = 0.6f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${weatherHistory.cityName}, ${weatherHistory.country}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = weatherHistory.description.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = formatTimestamp(weatherHistory.timestamp, weatherHistory.timezoneOffset),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Text(
                text = "${weatherHistory.temperature.toInt()}${Constants.Units.CELSIUS_SYMBOL}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long, timezoneOffsetSeconds: Int): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat(Constants.DateFormats.TIMESTAMP_FORMAT, Locale.getDefault())
    // Create a timezone with the offset from UTC
    val timeZone = java.util.TimeZone.getTimeZone(Constants.TimeZone.GMT)
    timeZone.rawOffset = timezoneOffsetSeconds * 1000
    formatter.timeZone = timeZone
    return formatter.format(date)
}

@Preview(showBackground = true)
@Composable
fun WeatherHistoryScreenPreview() {
    WeatherHistoryContent(
        uiState = WeatherHistoryUiState(),
        onSignOut = {},
        onRetry = {}
    )
}
