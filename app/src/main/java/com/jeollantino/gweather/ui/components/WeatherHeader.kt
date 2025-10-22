package com.jeollantino.gweather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeollantino.gweather.ui.icons.LocationPin
import com.jeollantino.gweather.ui.icons.WeatherIconWithGlow
import com.jeollantino.gweather.ui.theme.WeatherTextTertiary
import com.jeollantino.gweather.ui.theme.WeatherTypography

@Composable
fun WeatherHeader(
    location: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Location pin icon
        WeatherIconWithGlow(
            icon = LocationPin,
            contentDescription = "Location",
            tint = Color(0xFF00BFFF), // Light blue
            modifier = Modifier.semantics {
                contentDescription = "Location pin for $location"
            }
        )
        
        // Location text
        Text(
            text = location,
            style = WeatherTypography.labelMedium,
            color = WeatherTextTertiary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.semantics {
                contentDescription = "Current location: $location"
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1B3A)
@Composable
fun WeatherHeaderPreview() {
    WeatherHeader(location = "Singapore, Singapore")
}
