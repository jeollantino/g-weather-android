package com.jeollantino.gweather.domain.repository

import com.jeollantino.gweather.domain.model.LocationInfo
import com.jeollantino.gweather.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(location: LocationInfo): Result<WeatherInfo>
    suspend fun getCurrentWeatherByCity(cityName: String): Result<WeatherInfo>

    fun getCachedWeather(cityId: Int): Flow<WeatherInfo?>
    fun getCachedWeatherByCity(cityName: String): Flow<WeatherInfo?>
    fun getCachedWeatherByLocation(lat: Double, lon: Double): Flow<WeatherInfo?>
    
    suspend fun clearOldCache()
}
