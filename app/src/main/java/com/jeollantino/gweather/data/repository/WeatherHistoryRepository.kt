package com.jeollantino.gweather.data.repository

import com.jeollantino.gweather.data.local.dao.WeatherHistoryDao
import com.jeollantino.gweather.model.WeatherHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherHistoryRepository @Inject constructor(
    private val weatherHistoryDao: WeatherHistoryDao
) {
    fun getWeatherHistory(userId: Long): Flow<List<WeatherHistory>> {
        return weatherHistoryDao.getWeatherHistoryByUser(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun saveWeatherHistory(weatherHistory: WeatherHistory): Long {
        val entity = weatherHistory.toEntity()
        return weatherHistoryDao.insertWeatherHistory(entity)
    }

    suspend fun clearOldHistory(cutoffTime: Long) {
        weatherHistoryDao.deleteOldWeatherHistory(cutoffTime)
    }

    private fun WeatherHistory.toEntity(): com.jeollantino.gweather.data.local.entity.WeatherHistory {
        return com.jeollantino.gweather.data.local.entity.WeatherHistory(
            id = this.id,
            userId = this.userId,
            cityName = this.cityName,
            country = this.country,
            temperature = this.temperature,
            description = this.description,
            icon = this.icon,
            timestamp = this.timestamp,
            timezoneOffset = this.timezoneOffset
        )
    }

    private fun com.jeollantino.gweather.data.local.entity.WeatherHistory.toDomainModel(): WeatherHistory {
        return WeatherHistory(
            id = this.id,
            userId = this.userId,
            cityName = this.cityName,
            country = this.country,
            temperature = this.temperature,
            description = this.description,
            icon = this.icon,
            timestamp = this.timestamp,
            timezoneOffset = this.timezoneOffset
        )
    }
}
