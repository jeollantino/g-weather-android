package com.jeollantino.gweather.util

object Constants {

    // Date and Time Formats
    object DateFormats {
        const val TIME_FORMAT_12H = "h:mm a"
        const val TIMESTAMP_FORMAT = "MMM dd, yyyy hh:mm a"
    }

    // Timezone
    object TimeZone {
        const val UTC = "UTC"
        const val GMT = "GMT"
    }

    // Weather API
    object WeatherApi {
        const val ICON_URL_TEMPLATE = "https://openweathermap.org/img/wn/%s@4x.png"
    }

    // Temperature Units
    object Units {
        const val CELSIUS = "C"
        const val CELSIUS_SYMBOL = "°C"
        const val DEGREE_SYMBOL = "°"
        const val METERS_PER_SECOND = "m/s"
        const val HECTOPASCAL = "hPa"
        const val PERCENT = "%"
    }
}
