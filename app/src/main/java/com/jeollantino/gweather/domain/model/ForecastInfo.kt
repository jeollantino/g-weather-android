package com.jeollantino.gweather.domain.model

data class ForecastInfo(
    val id: Int,
    val cityName: String,
    val country: String,
    val forecastItems: List<ForecastItem>
)

data class ForecastItem(
    val dt: Long,
    val temperature: Double,
    val description: String,
    val icon: String
)

