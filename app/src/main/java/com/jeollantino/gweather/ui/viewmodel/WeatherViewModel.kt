package com.jeollantino.gweather.ui.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeollantino.gweather.data.repository.AuthRepository
import com.jeollantino.gweather.data.repository.WeatherHistoryRepository
import com.jeollantino.gweather.domain.model.LocationInfo
import com.jeollantino.gweather.domain.model.WeatherInfo
import com.jeollantino.gweather.domain.usecase.ClearCacheUseCase
import com.jeollantino.gweather.domain.usecase.GetCachedWeatherUseCase
import com.jeollantino.gweather.domain.usecase.GetCurrentWeatherUseCase
import com.jeollantino.gweather.util.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CurrentWeatherUiState(
    val isLoading: Boolean = false,
    val weatherInfo: WeatherInfo? = null,
    val error: String? = null,
    val cityName: String = "",
    val isManualEntry: Boolean = false
)

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getCachedWeatherUseCase: GetCachedWeatherUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val weatherHistoryRepository: WeatherHistoryRepository,
    private val authRepository: AuthRepository,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrentWeatherUiState())
    val uiState: StateFlow<CurrentWeatherUiState> = _uiState.asStateFlow()

    private var lastLocation: LocationInfo? = null

    // Permission is checked internally by LocationManager.getCurrentLocation()
    // which calls hasLocationPermission() before accessing location
    @SuppressLint("MissingPermission")
    fun loadWeatherByDeviceLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val location = locationManager.getCurrentLocation()
                if (location != null) {
                    val locationInfo = LocationInfo(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    lastLocation = locationInfo
                    loadWeatherByLocation(locationInfo)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Unable to get location. Please check location services."
                    )
                }
            } catch (e: SecurityException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Location permission denied. Please grant location access."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to get location"
                )
            }
        }
    }

    fun loadWeatherByLocation(location: LocationInfo) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            lastLocation = location

            try {
                val result = getCurrentWeatherUseCase(location)
                result.fold(
                    onSuccess = { weatherInfo ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherInfo = weatherInfo,
                            cityName = weatherInfo.cityName,
                            isManualEntry = false
                        )
                        saveWeatherToHistory(weatherInfo)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error occurred"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun loadWeatherByCity(cityName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, cityName = cityName, isManualEntry = true)

            try {
                val result = getCurrentWeatherUseCase(cityName)
                result.fold(
                    onSuccess = { weatherInfo ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherInfo = weatherInfo,
                            cityName = weatherInfo.cityName,
                            isManualEntry = true
                        )
                        saveWeatherToHistory(weatherInfo)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error occurred"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            clearCacheUseCase()
            if (_uiState.value.isManualEntry) {
                loadWeatherByCity(_uiState.value.cityName)
            } else {
                // Refresh with last location or device location
                lastLocation?.let { loadWeatherByLocation(it) } ?: loadWeatherByDeviceLocation()
            }
        }
    }

    private fun saveWeatherToHistory(weatherInfo: WeatherInfo) {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    weatherHistoryRepository.saveWeatherHistory(
                        com.jeollantino.gweather.model.WeatherHistory(
                            userId = currentUser.id,
                            cityName = weatherInfo.cityName,
                            country = weatherInfo.country,
                            temperature = weatherInfo.temperature,
                            description = weatherInfo.description,
                            icon = weatherInfo.icon,
                            timezoneOffset = weatherInfo.timezoneOffset
                        )
                    )
                }
            } catch (e: Exception) {
                // Handle error silently for background operation
            }
        }
    }
}
