package com.jeollantino.gweather.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeollantino.gweather.data.repository.AuthRepository
import com.jeollantino.gweather.data.repository.WeatherHistoryRepository
import com.jeollantino.gweather.model.WeatherHistory
import com.jeollantino.gweather.model.WeatherHistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherHistoryViewModel @Inject constructor(
    private val weatherHistoryRepository: WeatherHistoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherHistoryUiState())
    val uiState: StateFlow<WeatherHistoryUiState> = _uiState.asStateFlow()

    init {
        loadWeatherHistory()
    }

    fun loadWeatherHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    weatherHistoryRepository.getWeatherHistory(currentUser.id).collect { history ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherHistory = history
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "User not authenticated"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun saveWeatherHistory(
        cityName: String,
        country: String,
        temperature: Double,
        description: String,
        icon: String,
        timezoneOffset: Int = 0
    ) {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    val weatherHistory = WeatherHistory(
                        userId = currentUser.id,
                        cityName = cityName,
                        country = country,
                        temperature = temperature,
                        description = description,
                        icon = icon,
                        timezoneOffset = timezoneOffset
                    )
                    weatherHistoryRepository.saveWeatherHistory(weatherHistory)
                }
            } catch (e: Exception) {
                // Handle error silently for background operation
            }
        }
    }
}
