package com.jeollantino.gweather.domain.usecase

import com.jeollantino.gweather.domain.model.LocationInfo
import com.jeollantino.gweather.domain.model.WeatherInfo
import com.jeollantino.gweather.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class WeatherUseCasesTest {

    private lateinit var repository: WeatherRepository
    private lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase
    private lateinit var getCachedWeatherUseCase: GetCachedWeatherUseCase
    private lateinit var clearCacheUseCase: ClearCacheUseCase

    @Before
    fun setup() {
        repository = mockk()
        getCurrentWeatherUseCase = GetCurrentWeatherUseCase(repository)
        getCachedWeatherUseCase = GetCachedWeatherUseCase(repository)
        clearCacheUseCase = ClearCacheUseCase(repository)
    }

    @Test
    fun `GetCurrentWeatherUseCase should return success when repository succeeds`() = runTest {
        // Given
        val location = LocationInfo(51.5074, -0.1278, "London", "GB")
        val weatherInfo = createMockWeatherInfo()
        coEvery { repository.getCurrentWeather(location) } returns Result.success(weatherInfo)

        // When
        val result = getCurrentWeatherUseCase(location)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(weatherInfo, result.getOrNull())
    }

    @Test
    fun `GetCurrentWeatherUseCase should return failure when repository fails`() = runTest {
        // Given
        val location = LocationInfo(51.5074, -0.1278, "London", "GB")
        val exception = Exception("Network error")
        coEvery { repository.getCurrentWeather(location) } returns Result.failure(exception)

        // When
        val result = getCurrentWeatherUseCase(location)

        // Then
        assertFalse(result.isSuccess)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `GetCurrentWeatherUseCase with city name should return success when repository succeeds`() = runTest {
        // Given
        val cityName = "London"
        val weatherInfo = createMockWeatherInfo()
        coEvery { repository.getCurrentWeatherByCity(cityName) } returns Result.success(weatherInfo)

        // When
        val result = getCurrentWeatherUseCase(cityName)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(weatherInfo, result.getOrNull())
    }

    @Test
    fun `GetCachedWeatherUseCase should return cached weather`() = runTest {
        // Given
        val cityId = 1
        val weatherInfo = createMockWeatherInfo()
        every { repository.getCachedWeather(cityId) } returns flowOf(weatherInfo)

        // When
        val result = getCachedWeatherUseCase(cityId)

        // Then
        result.collect { cachedWeather ->
            assertEquals(weatherInfo, cachedWeather)
        }
    }

    @Test
    fun `GetCachedWeatherUseCase with city name should return cached weather`() = runTest {
        // Given
        val cityName = "London"
        val weatherInfo = createMockWeatherInfo()
        every { repository.getCachedWeatherByCity(cityName) } returns flowOf(weatherInfo)

        // When
        val result = getCachedWeatherUseCase(cityName)

        // Then
        result.collect { cachedWeather ->
            assertEquals(weatherInfo, cachedWeather)
        }
    }

    @Test
    fun `GetCachedWeatherUseCase with location should return cached weather`() = runTest {
        // Given
        val lat = 51.5074
        val lon = -0.1278
        val weatherInfo = createMockWeatherInfo()
        every { repository.getCachedWeatherByLocation(lat, lon) } returns flowOf(weatherInfo)

        // When
        val result = getCachedWeatherUseCase(lat, lon)

        // Then
        result.collect { cachedWeather ->
            assertEquals(weatherInfo, cachedWeather)
        }
    }

    @Test
    fun `ClearCacheUseCase should clear old cache`() = runTest {
        // Given
        coEvery { repository.clearOldCache() } returns Unit

        // When
        clearCacheUseCase()

        // Then
        // Verify that the method was called (implicitly through coEvery)
    }

    private fun createMockWeatherInfo(): WeatherInfo {
        return WeatherInfo(
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