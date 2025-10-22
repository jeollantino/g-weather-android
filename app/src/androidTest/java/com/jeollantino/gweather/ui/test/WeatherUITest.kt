package com.jeollantino.gweather.ui.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jeollantino.gweather.ui.screens.CurrentWeatherScreen
import com.jeollantino.gweather.ui.theme.WeatherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun weatherScreen_displaysAllElements() {
        composeTestRule.setContent {
            WeatherTheme {
                CurrentWeatherScreen()
            }
        }

        // Test that the weather card is displayed
        composeTestRule.onNodeWithTag("weather_card")
            .assertIsDisplayed()

        // Test that temperature is displayed
        composeTestRule.onNodeWithText("28°")
            .assertIsDisplayed()

        // Test that location is displayed
        composeTestRule.onNodeWithText("Singapore, Singapore")
            .assertIsDisplayed()

        // Test that sunrise time is displayed
        composeTestRule.onNodeWithText("6:30 AM")
            .assertIsDisplayed()

        // Test that sunset time is displayed
        composeTestRule.onNodeWithText("7:15 PM")
            .assertIsDisplayed()

        // Test that current time is displayed
        composeTestRule.onNodeWithText("11:57 PM")
            .assertIsDisplayed()
    }

    @Test
    fun weatherScreen_displaysWithCustomData() {
        composeTestRule.setContent {
            WeatherTheme {
                CurrentWeatherScreen(
                    location = "Tokyo, Japan",
                    temperature = "15°",
                    sunriseTime = "5:45 AM",
                    sunsetTime = "6:30 PM",
                    currentTime = "2:15 PM"
                )
            }
        }

        // Test custom data is displayed
        composeTestRule.onNodeWithText("Tokyo, Japan")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("15°")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("5:45 AM")
            .assertIsDisplayed()
    }
}
