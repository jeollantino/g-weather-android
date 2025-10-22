package com.jeollantino.gweather.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeollantino.gweather.ui.theme.WeatherTextPrimary
import com.jeollantino.gweather.ui.theme.WeatherTypography

@Composable
fun FooterTime(
    time: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = time,
        style = WeatherTypography.displaySmall,
        color = WeatherTextPrimary,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .semantics {
                contentDescription = "Current time: $time"
            }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1B3A)
@Composable
fun FooterTimePreview() {
    FooterTime(time = "11:57 PM")
}
