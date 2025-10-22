package com.jeollantino.gweather.domain.model

data class WeatherInfo(
    val id: Int,
    val cityName: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val description: String,
    val icon: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val visibility: Int,
    val sunrise: Long,
    val sunset: Long,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffset: Int // Timezone offset in seconds from UTC
)

data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val cityName: String? = null,
    val country: String? = null
)
