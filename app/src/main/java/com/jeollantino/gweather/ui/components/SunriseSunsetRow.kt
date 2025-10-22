package com.jeollantino.gweather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeollantino.gweather.R
import com.jeollantino.gweather.ui.icons.Sunrise
import com.jeollantino.gweather.ui.icons.Sunset
import com.jeollantino.gweather.ui.icons.WeatherIconWithGlow
import com.jeollantino.gweather.ui.theme.WeatherTextSecondary
import com.jeollantino.gweather.ui.theme.WeatherTypography

@Composable
fun SunriseSunsetRow(
    sunriseTime: String,
    sunsetTime: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Divider line
        HorizontalDivider(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 24.dp),
            thickness = 1.dp,
            color = WeatherTextSecondary.copy(alpha = 0.3f)
        )
        
        // Sunrise/Sunset row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Sunrise column
            SunriseSunsetColumn(
                icon = Sunrise,
                label = stringResource(R.string.sunrise),
                time = sunriseTime,
                contentDescription = stringResource(R.string.sunrise_time, sunriseTime),
                iconTint = Color(0xFFFFA500) // Orange
            )

            // Sunset column
            SunriseSunsetColumn(
                icon = Sunset,
                label = stringResource(R.string.sunset),
                time = sunsetTime,
                contentDescription = stringResource(R.string.sunset_time, sunsetTime),
                iconTint = Color(0xFFFFA500) // Orange
            )
        }
    }
}

@Composable
private fun SunriseSunsetColumn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    time: String,
    contentDescription: String,
    iconTint: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(120.dp)
            .semantics {
                this.contentDescription = contentDescription
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Icon
        WeatherIconWithGlow(
            icon = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
        )
        
        // Label
        Text(
            text = label,
            style = WeatherTypography.labelLarge,
            color = WeatherTextSecondary,
            textAlign = TextAlign.Center
        )
        
        // Time
        Text(
            text = time,
            style = WeatherTypography.bodyMedium,
            color = WeatherTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1B3A)
@Composable
fun SunriseSunsetRowPreview() {
    SunriseSunsetRow(
        sunriseTime = "6:30 AM",
        sunsetTime = "7:15 PM"
    )
}
