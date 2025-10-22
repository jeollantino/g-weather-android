package com.jeollantino.gweather.model

data class WeatherHistory(
    val id: Long = 0,
    val userId: Long,
    val cityName: String,
    val country: String,
    val temperature: Double,
    val description: String,
    val icon: String,
    val timestamp: Long = System.currentTimeMillis(),
    val timezoneOffset: Int = 0 // Timezone offset in seconds from UTC
)

data class WeatherHistoryUiState(
    val isLoading: Boolean = false,
    val weatherHistory: List<WeatherHistory> = emptyList(),
    val error: String? = null
)
