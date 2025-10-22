package com.jeollantino.gweather.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun WeatherTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = WeatherAccentTurquoise,
        secondary = WeatherTextSecondary,
        tertiary = WeatherTextTertiary,
        background = WeatherGradientTop,
        surface = WeatherCardBackground,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = WeatherTextPrimary,
        onSurface = WeatherTextPrimary
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WeatherTypography,
        content = content
    )
}
