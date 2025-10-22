package com.jeollantino.gweather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jeollantino.gweather.R
import com.jeollantino.gweather.ui.theme.*
import com.jeollantino.gweather.util.Constants

/**
 * Glassmorphism weather card with translucent background, inner glow, and soft shadows
 * Matches the reference image with exact styling and effects
 */
@Composable
fun WeatherCard(
    temperature: String,
    unit: String,
    sunriseTime: String,
    sunsetTime: String,
    icon: String,
    description: String = "",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(320.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = WeatherCardShadow,
                spotColor = WeatherCardShadow
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            WeatherCardBackground.copy(alpha = 0.3f),
                            WeatherCardBackground.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = WeatherCardBorder,
                    shape = RoundedCornerShape(24.dp)
                )
                .graphicsLayer {
                    // Inner glow effect simulation
                    alpha = 0.95f
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = String.format(Constants.WeatherApi.ICON_URL_TEMPLATE, icon),
                    contentDescription = description,
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit
                )

                // Content descriptions for accessibility
                val tempDescription = stringResource(R.string.temperature_description, temperature)
                val unitDescription = stringResource(R.string.temperature_unit_description, unit)

                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = temperature,
                        style = WeatherTypography.displayLarge,
                        color = WeatherTextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .semantics {
                                contentDescription = tempDescription
                            }
                    )

                    Text(
                        text = unit,
                        style = WeatherTypography.headlineMedium,
                        color = WeatherTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .semantics {
                                contentDescription = unitDescription
                            }
                    )
                }
                
                // Sunrise/Sunset information
                SunriseSunsetRow(
                    sunriseTime = sunriseTime,
                    sunsetTime = sunsetTime
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1B3A)
@Composable
fun WeatherCardPreview() {
    WeatherCard(
        temperature = "28°",
        unit = "C",
        sunriseTime = "6:30 AM",
        sunsetTime = "7:15 PM",
        icon = "01d",
        description = "clear sky"
    )
}