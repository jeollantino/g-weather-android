package com.jeollantino.gweather.data.repository

import com.jeollantino.gweather.data.api.OpenWeatherApiService
import com.jeollantino.gweather.data.local.dao.WeatherDao
import com.jeollantino.gweather.data.local.entity.WeatherCacheEntity
import com.jeollantino.gweather.data.model.WeatherResponse
import com.jeollantino.gweather.domain.model.LocationInfo
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {

    private lateinit var repository: WeatherRepositoryImpl
    private lateinit var apiService: OpenWeatherApiService
    private lateinit var weatherDao: WeatherDao
    private val apiKey = "test_api_key"

    @Before
    fun setup() {
        apiService = mockk()
        weatherDao = mockk()
        repository = WeatherRepositoryImpl(apiService, weatherDao, apiKey)
    }

    @Test
    fun `getCurrentWeather should return success when API call succeeds`() = runTest {
        // Given
        val location = LocationInfo(51.5074, -0.1278, "London", "GB")
        val weatherResponse = createMockWeatherResponse()
        
        coEvery { apiService.getCurrentWeather(any(), any(), any()) } returns weatherResponse
        coEvery { weatherDao.insertWeather(any()) } just Runs

        // When
        val result = repository.getCurrentWeather(location)

        // Then
        assertTrue(result.isSuccess)
        val weatherInfo = result.getOrNull()
        assertEquals("London", weatherInfo?.cityName)
        assertEquals("GB", weatherInfo?.country)
        assertEquals(20.0, weatherInfo?.temperature)
    }

    @Test
    fun `getCurrentWeather should return failure when API call fails`() = runTest {
        // Given
        val location = LocationInfo(51.5074, -0.1278, "London", "GB")
        
        coEvery { apiService.getCurrentWeather(any(), any(), any()) } throws Exception("API Error")

        // When
        val result = repository.getCurrentWeather(location)

        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull()?.message?.contains("API Error") == true)
    }

    @Test
    fun `getCurrentWeatherByCity should return success when API call succeeds`() = runTest {
        // Given
        val cityName = "London"
        val weatherResponse = createMockWeatherResponse()
        
        coEvery { apiService.getCurrentWeatherByCity(any(), any()) } returns weatherResponse
        coEvery { weatherDao.insertWeather(any()) } just Runs

        // When
        val result = repository.getCurrentWeatherByCity(cityName)

        // Then
        assertTrue(result.isSuccess)
        val weatherInfo = result.getOrNull()
        assertEquals("London", weatherInfo?.cityName)
    }

    @Test
    fun `getCachedWeather should return cached weather`() = runTest {
        // Given
        val cityId = 1
        val cachedWeather = createMockWeatherCacheEntity()
        every { weatherDao.getWeatherByCityId(cityId) } returns flowOf(cachedWeather)

        // When
        val result = repository.getCachedWeather(cityId)

        // Then
        result.collect { weatherInfo ->
            assertEquals("London", weatherInfo?.cityName)
            assertEquals("GB", weatherInfo?.country)
        }
    }

    @Test
    fun `clearOldCache should delete old cache entries`() = runTest {
        // Given
        coEvery { weatherDao.deleteOldWeatherCache(any()) } just Runs

        // When
        repository.clearOldCache()

        // Then
        coVerify { weatherDao.deleteOldWeatherCache(any()) }
    }

    private fun createMockWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            coord = com.jeollantino.gweather.data.model.Coord(51.5074, -0.1278),
            weather = listOf(
                com.jeollantino.gweather.data.model.Weather(
                    id = 800,
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            ),
            base = "stations",
            main = com.jeollantino.gweather.data.model.Main(
                temp = 20.0,
                feelsLike = 19.0,
                tempMin = 18.0,
                tempMax = 22.0,
                pressure = 1013,
                humidity = 65
            ),
            visibility = 10000,
            wind = com.jeollantino.gweather.data.model.Wind(speed = 3.5, deg = 180),
            clouds = com.jeollantino.gweather.data.model.Clouds(all = 0),
            dt = 1640995200,
            sys = com.jeollantino.gweather.data.model.Sys(
                type = 1,
                id = 1414,
                country = "GB",
                sunrise = 1640952000,
                sunset = 1640988000
            ),
            timezone = 0,
            id = 1,
            name = "London",
            cod = 200
        )
    }

    private fun createMockWeatherCacheEntity(): WeatherCacheEntity {
        return WeatherCacheEntity(
            id = 1,
            cityName = "London",
            country = "GB",
            temperature = 20.0,
            feelsLike = 19.0,
            minTemp = 18.0,
            maxTemp = 22.0,
            description = "clear sky",
            icon = "01d",
            humidity = 65,
            pressure = 1013,
            windSpeed = 3.5,
            windDirection = 180,
            visibility = 10000,
            sunrise = 1640952000,
            sunset = 1640988000,
            timestamp = 1640995200,
            latitude = 51.5074,
            longitude = -0.1278,
            timezoneOffset = 0
        )
    }
}
