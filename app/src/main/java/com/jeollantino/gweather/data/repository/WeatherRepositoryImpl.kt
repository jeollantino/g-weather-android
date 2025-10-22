package com.jeollantino.gweather.data.repository

import com.jeollantino.gweather.data.api.OpenWeatherApiService
import com.jeollantino.gweather.data.local.dao.WeatherDao
import com.jeollantino.gweather.data.mapper.toCacheEntity
import com.jeollantino.gweather.data.mapper.toDomainModel
import com.jeollantino.gweather.domain.model.LocationInfo
import com.jeollantino.gweather.domain.model.WeatherInfo
import com.jeollantino.gweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: OpenWeatherApiService,
    private val weatherDao: WeatherDao,
    private val apiKey: String
) : WeatherRepository {

    companion object {
        private const val CACHE_DURATION_MS = 10 * 60 * 1000L // 10 minutes
    }

    override suspend fun getCurrentWeather(location: LocationInfo): Result<WeatherInfo> {
        return try {
            val response = apiService.getCurrentWeather(
                latitude = location.latitude,
                longitude = location.longitude,
                apiKey = apiKey
            )
            
            val weatherInfo = response.toDomainModel()
            
            // Cache the result
            weatherDao.insertWeather(weatherInfo.toCacheEntity())
            
            Result.success(weatherInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentWeatherByCity(cityName: String): Result<WeatherInfo> {
        return try {
            val response = apiService.getCurrentWeatherByCity(
                cityName = cityName,
                apiKey = apiKey
            )
            
            val weatherInfo = response.toDomainModel()
            
            // Cache the result
            weatherDao.insertWeather(weatherInfo.toCacheEntity())
            
            Result.success(weatherInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCachedWeather(cityId: Int): Flow<WeatherInfo?> {
        return weatherDao.getWeatherByCityId(cityId).map { it?.toDomainModel() }
    }

    override fun getCachedWeatherByCity(cityName: String): Flow<WeatherInfo?> {
        return weatherDao.getWeatherByCityName(cityName).map { it?.toDomainModel() }
    }

    override fun getCachedWeatherByLocation(lat: Double, lon: Double): Flow<WeatherInfo?> {
        return weatherDao.getWeatherByLocation(lat, lon).map { it?.toDomainModel() }
    }

    override suspend fun clearOldCache() {
        val cutoffTime = System.currentTimeMillis() - CACHE_DURATION_MS
        weatherDao.deleteOldWeatherCache(cutoffTime)
    }
}
