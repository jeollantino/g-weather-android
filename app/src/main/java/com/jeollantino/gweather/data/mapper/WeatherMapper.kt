package com.jeollantino.gweather.data.mapper

import com.jeollantino.gweather.data.local.entity.WeatherCacheEntity
import com.jeollantino.gweather.data.model.WeatherResponse
import com.jeollantino.gweather.domain.model.WeatherInfo

fun WeatherResponse.toDomainModel(): WeatherInfo {
    return WeatherInfo(
        id = id,
        cityName = name,
        country = sys.country,
        temperature = main.temp,
        feelsLike = main.feelsLike,
        minTemp = main.tempMin,
        maxTemp = main.tempMax,
        description = weather.firstOrNull()?.description ?: "",
        icon = weather.firstOrNull()?.icon ?: "",
        humidity = main.humidity,
        pressure = main.pressure,
        windSpeed = wind.speed,
        windDirection = wind.deg,
        visibility = visibility,
        sunrise = sys.sunrise,
        sunset = sys.sunset,
        timestamp = dt,
        latitude = coord.lat,
        longitude = coord.lon,
        timezoneOffset = timezone
    )
}

fun WeatherInfo.toCacheEntity(): WeatherCacheEntity {
    return WeatherCacheEntity(
        id = id,
        cityName = cityName,
        country = country,
        temperature = temperature,
        feelsLike = feelsLike,
        minTemp = minTemp,
        maxTemp = maxTemp,
        description = description,
        icon = icon,
        humidity = humidity,
        pressure = pressure,
        windSpeed = windSpeed,
        windDirection = windDirection,
        visibility = visibility,
        sunrise = sunrise,
        sunset = sunset,
        timestamp = timestamp,
        latitude = latitude,
        longitude = longitude,
        timezoneOffset = timezoneOffset
    )
}

fun WeatherCacheEntity.toDomainModel(): WeatherInfo {
    return WeatherInfo(
        id = id,
        cityName = cityName,
        country = country,
        temperature = temperature,
        feelsLike = feelsLike,
        minTemp = minTemp,
        maxTemp = maxTemp,
        description = description,
        icon = icon,
        humidity = humidity,
        pressure = pressure,
        windSpeed = windSpeed,
        windDirection = windDirection,
        visibility = visibility,
        sunrise = sunrise,
        sunset = sunset,
        timestamp = timestamp,
        latitude = latitude,
        longitude = longitude,
        timezoneOffset = timezoneOffset
    )
}
