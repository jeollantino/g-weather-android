package com.jeollantino.gweather.data.local.dao

import androidx.room.*
import com.jeollantino.gweather.data.local.entity.WeatherCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_cache WHERE id = :cityId")
    fun getWeatherByCityId(cityId: Int): Flow<WeatherCacheEntity?>

    @Query("SELECT * FROM weather_cache WHERE cityName = :cityName")
    fun getWeatherByCityName(cityName: String): Flow<WeatherCacheEntity?>

    @Query("SELECT * FROM weather_cache WHERE latitude = :lat AND longitude = :lon")
    fun getWeatherByLocation(lat: Double, lon: Double): Flow<WeatherCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherCacheEntity)

    @Query("DELETE FROM weather_cache WHERE id = :cityId")
    suspend fun deleteWeatherByCityId(cityId: Int)

    @Query("DELETE FROM weather_cache WHERE cachedAt < :timestamp")
    suspend fun deleteOldWeatherCache(timestamp: Long)
}
