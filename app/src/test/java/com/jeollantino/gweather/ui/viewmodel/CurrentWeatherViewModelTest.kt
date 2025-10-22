package com.jeollantino.gweather.ui.viewmodel

import android.location.Location
import com.jeollantino.gweather.data.repository.AuthRepository
import com.jeollantino.gweather.data.repository.WeatherHistoryRepository
import com.jeollantino.gweather.domain.model.LocationInfo
import com.jeollantino.gweather.domain.model.WeatherInfo
import com.jeollantino.gweather.domain.usecase.ClearCacheUseCase
import com.jeollantino.gweather.domain.usecase.GetCachedWeatherUseCase
import com.jeollantino.gweather.domain.usecase.GetCurrentWeatherUseCase
import com.jeollantino.gweather.model.User
import com.jeollantino.gweather.util.LocationManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrentWeatherViewModelTest {

    private lateinit var viewModel: CurrentWeatherViewModel
    private lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase
    private lateinit var getCachedWeatherUseCase: GetCachedWeatherUseCase
    private lateinit var clearCacheUseCase: ClearCacheUseCase
    private lateinit var weatherHistoryRepository: WeatherHistoryRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var locationManager: LocationManager

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getCurrentWeatherUseCase = mockk()
        getCachedWeatherUseCase = mockk()
        clearCacheUseCase = mockk()
        weatherHistoryRepository = mockk()
        authRepository = mockk()
        locationManager = mockk()

        viewModel = CurrentWeatherViewModel(
            getCurrentWeatherUseCase,
            getCachedWeatherUseCase,
            clearCacheUseCase,
            weatherHistoryRepository,
            authRepository,
            locationManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadWeatherByDeviceLocation should update state with weather info when location is available`() = runTest {
        // Given
        val mockLocation = mockk<Location> {
            every { latitude } returns 51.5074
            every { longitude } returns -0.1278
        }
        val weatherInfo = createMockWeatherInfo()
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")

        coEvery { locationManager.getCurrentLocation() } returns mockLocation
        coEvery { getCurrentWeatherUseCase(any<LocationInfo>()) } returns Result.success(weatherInfo)
        coEvery { authRepository.getCurrentUser() } returns user
        coEvery { weatherHistoryRepository.saveWeatherHistory(any()) } returns 1L

        // When
        viewModel.loadWeatherByDeviceLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(weatherInfo, state.weatherInfo)
        assertNull(state.error)
        assertEquals("London", state.cityName)
        assertFalse(state.isManualEntry)
    }

    @Test
    fun `loadWeatherByDeviceLocation should update state with error when location is null`() = runTest {
        // Given
        coEvery { locationManager.getCurrentLocation() } returns null

        // When
        viewModel.loadWeatherByDeviceLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.weatherInfo)
        assertEquals("Unable to get location. Please check location services.", state.error)
    }

    @Test
    fun `loadWeatherByDeviceLocation should update state with error when exception occurs`() = runTest {
        // Given
        coEvery { locationManager.getCurrentLocation() } throws Exception("Location error")

        // When
        viewModel.loadWeatherByDeviceLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.weatherInfo)
        assertEquals("Location error", state.error)
    }

    @Test
    fun `loadWeatherByLocation should update state with weather info on success`() = runTest {
        // Given
        val location = LocationInfo(51.5074, -0.1278, "London", "GB")
        val weatherInfo = createMockWeatherInfo()
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")

        coEvery { getCurrentWeatherUseCase(location) } returns Result.success(weatherInfo)
        coEvery { authRepository.getCurrentUser() } returns user
        coEvery { weatherHistoryRepository.saveWeatherHistory(any()) } returns 1L

        // When
        viewModel.loadWeatherByLocation(location)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(weatherInfo, state.weatherInfo)
        assertNull(state.error)
        assertEquals("London", state.cityName)
        assertFalse(state.isManualEntry)
    }

    @Test
    fun `loadWeatherByLocation should update state with error on failure`() = runTest {
        // Given
        val location = LocationInfo(51.5074, -0.1278, "London", "GB")
        val error = Exception("Network error")

        coEvery { getCurrentWeatherUseCase(location) } returns Result.failure(error)

        // When
        viewModel.loadWeatherByLocation(location)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.weatherInfo)
        assertEquals("Network error", state.error)
    }

    @Test
    fun `loadWeatherByCity should update state with weather info on success`() = runTest {
        // Given
        val cityName = "London"
        val weatherInfo = createMockWeatherInfo()
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")

        coEvery { getCurrentWeatherUseCase(cityName) } returns Result.success(weatherInfo)
        coEvery { authRepository.getCurrentUser() } returns user
        coEvery { weatherHistoryRepository.saveWeatherHistory(any()) } returns 1L

        // When
        viewModel.loadWeatherByCity(cityName)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(weatherInfo, state.weatherInfo)
        assertNull(state.error)
        assertEquals("London", state.cityName)
        assertTrue(state.isManualEntry)
    }

    @Test
    fun `loadWeatherByCity should update state with error on failure`() = runTest {
        // Given
        val cityName = "InvalidCity"
        val error = Exception("City not found")

        coEvery { getCurrentWeatherUseCase(cityName) } returns Result.failure(error)

        // When
        viewModel.loadWeatherByCity(cityName)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.weatherInfo)
        assertEquals("City not found", state.error)
        assertTrue(state.isManualEntry)
    }

    @Test
    fun `refresh should reload weather by city when isManualEntry is true`() = runTest {
        // Given
        val cityName = "London"
        val weatherInfo = createMockWeatherInfo()
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")

        coEvery { getCurrentWeatherUseCase(cityName) } returns Result.success(weatherInfo)
        coEvery { authRepository.getCurrentUser() } returns user
        coEvery { weatherHistoryRepository.saveWeatherHistory(any()) } returns 1L
        coEvery { clearCacheUseCase() } just Runs

        // First load to set isManualEntry to true
        viewModel.loadWeatherByCity(cityName)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { clearCacheUseCase() }
        coVerify(exactly = 2) { getCurrentWeatherUseCase(cityName) }
    }

    @Test
    fun `refresh should reload weather by location when isManualEntry is false`() = runTest {
        // Given
        val location = LocationInfo(51.5074, -0.1278, "London", "GB")
        val weatherInfo = createMockWeatherInfo()
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")

        coEvery { getCurrentWeatherUseCase(location) } returns Result.success(weatherInfo)
        coEvery { authRepository.getCurrentUser() } returns user
        coEvery { weatherHistoryRepository.saveWeatherHistory(any()) } returns 1L
        coEvery { clearCacheUseCase() } just Runs

        // First load to set last location
        viewModel.loadWeatherByLocation(location)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { clearCacheUseCase() }
        coVerify(exactly = 2) { getCurrentWeatherUseCase(location) }
    }

    @Test
    fun `saveWeatherToHistory should save history when user is logged in`() = runTest {
        // Given
        val location = LocationInfo(51.5074, -0.1278, "London", "GB")
        val weatherInfo = createMockWeatherInfo()
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")

        coEvery { getCurrentWeatherUseCase(location) } returns Result.success(weatherInfo)
        coEvery { authRepository.getCurrentUser() } returns user
        coEvery { weatherHistoryRepository.saveWeatherHistory(any()) } returns 1L

        // When
        viewModel.loadWeatherByLocation(location)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            weatherHistoryRepository.saveWeatherHistory(
                match {
                    it.userId == user.id &&
                    it.cityName == weatherInfo.cityName &&
                    it.temperature == weatherInfo.temperature
                }
            )
        }
    }

    @Test
    fun `saveWeatherToHistory should not save history when user is not logged in`() = runTest {
        // Given
        val location = LocationInfo(51.5074, -0.1278, "London", "GB")
        val weatherInfo = createMockWeatherInfo()

        coEvery { getCurrentWeatherUseCase(location) } returns Result.success(weatherInfo)
        coEvery { authRepository.getCurrentUser() } returns null

        // When
        viewModel.loadWeatherByLocation(location)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { weatherHistoryRepository.saveWeatherHistory(any()) }
    }

    @Test
    fun `initial state should be correct`() {
        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.weatherInfo)
        assertNull(state.error)
        assertEquals("", state.cityName)
        assertFalse(state.isManualEntry)
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
