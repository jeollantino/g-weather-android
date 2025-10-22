package com.jeollantino.gweather.ui.viewmodel

import com.jeollantino.gweather.data.repository.AuthRepository
import com.jeollantino.gweather.data.repository.WeatherHistoryRepository
import com.jeollantino.gweather.model.User
import com.jeollantino.gweather.model.WeatherHistory
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherHistoryViewModelTest {

    private lateinit var viewModel: WeatherHistoryViewModel
    private lateinit var weatherHistoryRepository: WeatherHistoryRepository
    private lateinit var authRepository: AuthRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        weatherHistoryRepository = mockk()
        authRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load weather history when user is authenticated`() = runTest {
        // Given
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")
        val weatherHistory = listOf(
            createWeatherHistory(userId = 1L, cityName = "London"),
            createWeatherHistory(userId = 1L, cityName = "Paris")
        )

        coEvery { authRepository.getCurrentUser() } returns user
        every { weatherHistoryRepository.getWeatherHistory(user.id) } returns flowOf(weatherHistory)

        // When
        viewModel = WeatherHistoryViewModel(weatherHistoryRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.weatherHistory.size)
        assertNull(state.error)
    }

    @Test
    fun `init should show error when user is not authenticated`() = runTest {
        // Given
        coEvery { authRepository.getCurrentUser() } returns null

        // When
        viewModel = WeatherHistoryViewModel(weatherHistoryRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.weatherHistory.isEmpty())
        assertEquals("User not authenticated", state.error)
    }

    @Test
    fun `init should show error when repository throws exception`() = runTest {
        // Given
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")
        val errorMessage = "Database error"

        coEvery { authRepository.getCurrentUser() } returns user
        every { weatherHistoryRepository.getWeatherHistory(user.id) } throws Exception(errorMessage)

        // When
        viewModel = WeatherHistoryViewModel(weatherHistoryRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.weatherHistory.isEmpty())
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `loadWeatherHistory should update state with weather history`() = runTest {
        // Given
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")
        val weatherHistory = listOf(createWeatherHistory(userId = 1L, cityName = "Tokyo"))

        coEvery { authRepository.getCurrentUser() } returns user
        every { weatherHistoryRepository.getWeatherHistory(user.id) } returns flowOf(weatherHistory)

        viewModel = WeatherHistoryViewModel(weatherHistoryRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.loadWeatherHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.weatherHistory.size)
        assertEquals("Tokyo", state.weatherHistory[0].cityName)
        assertNull(state.error)
    }

    @Test
    fun `loadWeatherHistory should handle empty history`() = runTest {
        // Given
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")
        val emptyHistory = emptyList<WeatherHistory>()

        coEvery { authRepository.getCurrentUser() } returns user
        every { weatherHistoryRepository.getWeatherHistory(user.id) } returns flowOf(emptyHistory)

        viewModel = WeatherHistoryViewModel(weatherHistoryRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.loadWeatherHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.weatherHistory.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `loadWeatherHistory should update state continuously when Flow emits new values`() = runTest {
        // Given
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")
        val weatherHistory1 = listOf(createWeatherHistory(userId = 1L, cityName = "London"))
        val weatherHistory2 = listOf(
            createWeatherHistory(userId = 1L, cityName = "London"),
            createWeatherHistory(userId = 1L, cityName = "Paris")
        )

        coEvery { authRepository.getCurrentUser() } returns user
        every { weatherHistoryRepository.getWeatherHistory(user.id) } returns flowOf(weatherHistory1, weatherHistory2)

        // When
        viewModel = WeatherHistoryViewModel(weatherHistoryRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.weatherHistory.size)
    }

    @Test
    fun `saveWeatherHistory should call repository when user is authenticated`() = runTest {
        // Given
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")
        val cityName = "London"
        val country = "GB"
        val temperature = 20.0
        val description = "clear sky"
        val icon = "01d"
        val timezoneOffset = 0

        coEvery { authRepository.getCurrentUser() } returns user
        every { weatherHistoryRepository.getWeatherHistory(user.id) } returns flowOf(emptyList())
        coEvery { weatherHistoryRepository.saveWeatherHistory(any()) } returns 1L

        viewModel = WeatherHistoryViewModel(weatherHistoryRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.saveWeatherHistory(cityName, country, temperature, description, icon, timezoneOffset)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            weatherHistoryRepository.saveWeatherHistory(
                match { history ->
                    history.userId == user.id &&
                    history.cityName == cityName &&
                    history.country == country &&
                    history.temperature == temperature &&
                    history.description == description &&
                    history.icon == icon &&
                    history.timezoneOffset == timezoneOffset
                }
            )
        }
    }

    @Test
    fun `saveWeatherHistory should not call repository when user is not authenticated`() = runTest {
        // Given
        coEvery { authRepository.getCurrentUser() } returns null

        viewModel = WeatherHistoryViewModel(weatherHistoryRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.saveWeatherHistory("London", "GB", 20.0, "clear", "01d")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { weatherHistoryRepository.saveWeatherHistory(any()) }
    }

    @Test
    fun `saveWeatherHistory should use default timezone offset when not provided`() = runTest {
        // Given
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")

        coEvery { authRepository.getCurrentUser() } returns user
        every { weatherHistoryRepository.getWeatherHistory(user.id) } returns flowOf(emptyList())
        coEvery { weatherHistoryRepository.saveWeatherHistory(any()) } returns 1L

        viewModel = WeatherHistoryViewModel(weatherHistoryRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.saveWeatherHistory("London", "GB", 20.0, "clear", "01d")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            weatherHistoryRepository.saveWeatherHistory(
                match { history ->
                    history.timezoneOffset == 0
                }
            )
        }
    }

    @Test
    fun `saveWeatherHistory should handle exception silently`() = runTest {
        // Given
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")

        coEvery { authRepository.getCurrentUser() } returns user
        every { weatherHistoryRepository.getWeatherHistory(user.id) } returns flowOf(emptyList())
        coEvery { weatherHistoryRepository.saveWeatherHistory(any()) } throws Exception("Save error")

        viewModel = WeatherHistoryViewModel(weatherHistoryRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - Should not throw exception
        viewModel.saveWeatherHistory("London", "GB", 20.0, "clear", "01d")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - State should remain unchanged (no error shown)
        val state = viewModel.uiState.value
        assertNull(state.error)
    }

    private fun createWeatherHistory(
        id: Long = 1L,
        userId: Long,
        cityName: String,
        country: String = "GB",
        temperature: Double = 20.0,
        description: String = "clear sky",
        icon: String = "01d",
        timestamp: Long = System.currentTimeMillis(),
        timezoneOffset: Int = 0
    ): WeatherHistory {
        return WeatherHistory(
            id = id,
            userId = userId,
            cityName = cityName,
            country = country,
            temperature = temperature,
            description = description,
            icon = icon,
            timestamp = timestamp,
            timezoneOffset = timezoneOffset
        )
    }
}
