package com.jeollantino.gweather.data.mapper

import com.jeollantino.gweather.data.local.entity.WeatherCacheEntity
import com.jeollantino.gweather.data.model.*
import com.jeollantino.gweather.domain.model.WeatherInfo
import org.junit.Test
import org.junit.Assert.*

class WeatherMapperTest {

    @Test
    fun `WeatherResponse toDomainModel should map all fields correctly`() {
        // Given
        val weatherResponse = WeatherResponse(
            coord = Coord(lat = 51.5074, lon = -0.1278),
            weather = listOf(
                Weather(
                    id = 800,
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            ),
            base = "stations",
            main = Main(
                temp = 20.0,
                feelsLike = 19.0,
                tempMin = 18.0,
                tempMax = 22.0,
                pressure = 1013,
                humidity = 65
            ),
            visibility = 10000,
            wind = Wind(speed = 3.5, deg = 180),
            clouds = Clouds(all = 0),
            dt = 1640995200,
            sys = Sys(
                type = 1,
                id = 1414,
                country = "GB",
                sunrise = 1640952000,
                sunset = 1640988000
            ),
            timezone = 0,
            id = 2643743,
            name = "London",
            cod = 200
        )

        // When
        val domainModel = weatherResponse.toDomainModel()

        // Then
        assertEquals(2643743, domainModel.id)
        assertEquals("London", domainModel.cityName)
        assertEquals("GB", domainModel.country)
        assertEquals(20.0, domainModel.temperature, 0.001)
        assertEquals(19.0, domainModel.feelsLike, 0.001)
        assertEquals(18.0, domainModel.minTemp, 0.001)
        assertEquals(22.0, domainModel.maxTemp, 0.001)
        assertEquals("clear sky", domainModel.description)
        assertEquals("01d", domainModel.icon)
        assertEquals(65, domainModel.humidity)
        assertEquals(1013, domainModel.pressure)
        assertEquals(3.5, domainModel.windSpeed, 0.001)
        assertEquals(180, domainModel.windDirection)
        assertEquals(10000, domainModel.visibility)
        assertEquals(1640952000, domainModel.sunrise)
        assertEquals(1640988000, domainModel.sunset)
        assertEquals(1640995200, domainModel.timestamp)
        assertEquals(51.5074, domainModel.latitude, 0.0001)
        assertEquals(-0.1278, domainModel.longitude, 0.0001)
        assertEquals(0, domainModel.timezoneOffset)
    }

    @Test
    fun `WeatherResponse toDomainModel should handle empty weather list`() {
        // Given
        val weatherResponse = createWeatherResponse(emptyList())

        // When
        val domainModel = weatherResponse.toDomainModel()

        // Then
        assertEquals("", domainModel.description)
        assertEquals("", domainModel.icon)
    }

    @Test
    fun `WeatherResponse toDomainModel should use first weather item when multiple exist`() {
        // Given
        val weatherList = listOf(
            Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d"),
            Weather(id = 801, main = "Clouds", description = "few clouds", icon = "02d")
        )
        val weatherResponse = createWeatherResponse(weatherList)

        // When
        val domainModel = weatherResponse.toDomainModel()

        // Then
        assertEquals("clear sky", domainModel.description)
        assertEquals("01d", domainModel.icon)
    }

    @Test
    fun `WeatherInfo toCacheEntity should map all fields correctly`() {
        // Given
        val weatherInfo = WeatherInfo(
            id = 2643743,
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

        // When
        val cacheEntity = weatherInfo.toCacheEntity()

        // Then
        assertEquals(2643743, cacheEntity.id)
        assertEquals("London", cacheEntity.cityName)
        assertEquals("GB", cacheEntity.country)
        assertEquals(20.0, cacheEntity.temperature, 0.001)
        assertEquals(19.0, cacheEntity.feelsLike, 0.001)
        assertEquals(18.0, cacheEntity.minTemp, 0.001)
        assertEquals(22.0, cacheEntity.maxTemp, 0.001)
        assertEquals("clear sky", cacheEntity.description)
        assertEquals("01d", cacheEntity.icon)
        assertEquals(65, cacheEntity.humidity)
        assertEquals(1013, cacheEntity.pressure)
        assertEquals(3.5, cacheEntity.windSpeed, 0.001)
        assertEquals(180, cacheEntity.windDirection)
        assertEquals(10000, cacheEntity.visibility)
        assertEquals(1640952000, cacheEntity.sunrise)
        assertEquals(1640988000, cacheEntity.sunset)
        assertEquals(1640995200, cacheEntity.timestamp)
        assertEquals(51.5074, cacheEntity.latitude, 0.0001)
        assertEquals(-0.1278, cacheEntity.longitude, 0.0001)
        assertEquals(0, cacheEntity.timezoneOffset)
    }

    @Test
    fun `WeatherCacheEntity toDomainModel should map all fields correctly`() {
        // Given
        val cacheEntity = WeatherCacheEntity(
            id = 2643743,
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

        // When
        val domainModel = cacheEntity.toDomainModel()

        // Then
        assertEquals(2643743, domainModel.id)
        assertEquals("London", domainModel.cityName)
        assertEquals("GB", domainModel.country)
        assertEquals(20.0, domainModel.temperature, 0.001)
        assertEquals(19.0, domainModel.feelsLike, 0.001)
        assertEquals(18.0, domainModel.minTemp, 0.001)
        assertEquals(22.0, domainModel.maxTemp, 0.001)
        assertEquals("clear sky", domainModel.description)
        assertEquals("01d", domainModel.icon)
        assertEquals(65, domainModel.humidity)
        assertEquals(1013, domainModel.pressure)
        assertEquals(3.5, domainModel.windSpeed, 0.001)
        assertEquals(180, domainModel.windDirection)
        assertEquals(10000, domainModel.visibility)
        assertEquals(1640952000, domainModel.sunrise)
        assertEquals(1640988000, domainModel.sunset)
        assertEquals(1640995200, domainModel.timestamp)
        assertEquals(51.5074, domainModel.latitude, 0.0001)
        assertEquals(-0.1278, domainModel.longitude, 0.0001)
        assertEquals(0, domainModel.timezoneOffset)
    }

    @Test
    fun `round trip conversion WeatherInfo to CacheEntity to WeatherInfo should preserve data`() {
        // Given
        val originalWeatherInfo = WeatherInfo(
            id = 2643743,
            cityName = "London",
            country = "GB",
            temperature = 20.5,
            feelsLike = 19.3,
            minTemp = 18.2,
            maxTemp = 22.7,
            description = "partly cloudy",
            icon = "02d",
            humidity = 72,
            pressure = 1015,
            windSpeed = 4.2,
            windDirection = 225,
            visibility = 8500,
            sunrise = 1640952000,
            sunset = 1640988000,
            timestamp = 1640995200,
            latitude = 51.5074,
            longitude = -0.1278,
            timezoneOffset = 3600
        )

        // When
        val cacheEntity = originalWeatherInfo.toCacheEntity()
        val convertedWeatherInfo = cacheEntity.toDomainModel()

        // Then
        assertEquals(originalWeatherInfo, convertedWeatherInfo)
    }

    @Test
    fun `conversion should handle negative coordinates`() {
        // Given
        val weatherResponse = WeatherResponse(
            coord = Coord(lat = -33.8688, lon = 151.2093),
            weather = listOf(Weather(id = 800, main = "Clear", description = "clear", icon = "01d")),
            base = "stations",
            main = Main(temp = 25.0, feelsLike = 24.0, tempMin = 23.0, tempMax = 27.0, pressure = 1010, humidity = 60),
            visibility = 10000,
            wind = Wind(speed = 2.5, deg = 90),
            clouds = Clouds(all = 0),
            dt = 1640995200,
            sys = Sys(type = 1, id = 1, country = "AU", sunrise = 1640900000, sunset = 1640950000),
            timezone = 36000,
            id = 1,
            name = "Sydney",
            cod = 200
        )

        // When
        val domainModel = weatherResponse.toDomainModel()

        // Then
        assertEquals(-33.8688, domainModel.latitude, 0.0001)
        assertEquals(151.2093, domainModel.longitude, 0.0001)
    }

    @Test
    fun `conversion should handle negative timezone offset`() {
        // Given
        val weatherResponse = WeatherResponse(
            coord = Coord(lat = 40.7128, lon = -74.0060),
            weather = listOf(Weather(id = 800, main = "Clear", description = "clear", icon = "01d")),
            base = "stations",
            main = Main(temp = 15.0, feelsLike = 14.0, tempMin = 13.0, tempMax = 17.0, pressure = 1010, humidity = 55),
            visibility = 10000,
            wind = Wind(speed = 3.0, deg = 270),
            clouds = Clouds(all = 0),
            dt = 1640995200,
            sys = Sys(type = 1, id = 1, country = "US", sunrise = 1640950000, sunset = 1640990000),
            timezone = -18000, // -5 hours (EST)
            id = 1,
            name = "New York",
            cod = 200
        )

        // When
        val domainModel = weatherResponse.toDomainModel()

        // Then
        assertEquals(-18000, domainModel.timezoneOffset)
    }

    private fun createWeatherResponse(weatherList: List<Weather>): WeatherResponse {
        return WeatherResponse(
            coord = Coord(lat = 51.5074, lon = -0.1278),
            weather = weatherList,
            base = "stations",
            main = Main(temp = 20.0, feelsLike = 19.0, tempMin = 18.0, tempMax = 22.0, pressure = 1013, humidity = 65),
            visibility = 10000,
            wind = Wind(speed = 3.5, deg = 180),
            clouds = Clouds(all = 0),
            dt = 1640995200,
            sys = Sys(type = 1, id = 1414, country = "GB", sunrise = 1640952000, sunset = 1640988000),
            timezone = 0,
            id = 2643743,
            name = "London",
            cod = 200
        )
    }
}
