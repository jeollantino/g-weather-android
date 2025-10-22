package com.jeollantino.gweather.domain.usecase

import com.jeollantino.gweather.domain.model.LocationInfo
import com.jeollantino.gweather.domain.model.WeatherInfo
import com.jeollantino.gweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(location: LocationInfo): Result<WeatherInfo> {
        return repository.getCurrentWeather(location)
    }
    
    suspend operator fun invoke(cityName: String): Result<WeatherInfo> {
        return repository.getCurrentWeatherByCity(cityName)
    }
}

class GetCachedWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(cityId: Int): Flow<WeatherInfo?> {
        return repository.getCachedWeather(cityId)
    }
    
    operator fun invoke(cityName: String): Flow<WeatherInfo?> {
        return repository.getCachedWeatherByCity(cityName)
    }
    
    operator fun invoke(lat: Double, lon: Double): Flow<WeatherInfo?> {
        return repository.getCachedWeatherByLocation(lat, lon)
    }
}

class ClearCacheUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke() {
        repository.clearOldCache()
    }
}
