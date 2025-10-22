package com.jeollantino.gweather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
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
    val timezoneOffset: Int,
    val cachedAt: Long = System.currentTimeMillis()
)
