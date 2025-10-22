package com.jeollantino.gweather.data.local.dao

import androidx.room.*
import com.jeollantino.gweather.data.local.entity.WeatherHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherHistoryDao {
    @Query("SELECT * FROM weather_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getWeatherHistoryByUser(userId: Long): Flow<List<WeatherHistory>>

    @Query("SELECT * FROM weather_history WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentWeatherHistory(userId: Long, limit: Int = 10): Flow<List<WeatherHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherHistory(weatherHistory: WeatherHistory): Long

    @Query("DELETE FROM weather_history WHERE userId = :userId")
    suspend fun deleteWeatherHistoryByUser(userId: Long)

    @Query("DELETE FROM weather_history WHERE timestamp < :cutoffTime")
    suspend fun deleteOldWeatherHistory(cutoffTime: Long)
}
