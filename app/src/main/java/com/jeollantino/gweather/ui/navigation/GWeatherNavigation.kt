package com.jeollantino.gweather.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jeollantino.gweather.ui.screens.CurrentWeatherScreen
import com.jeollantino.gweather.ui.screens.LoginScreen
import com.jeollantino.gweather.ui.screens.RegisterScreen
import com.jeollantino.gweather.ui.screens.WeatherHistoryScreen
import com.jeollantino.gweather.ui.viewmodel.AuthViewModel
import androidx.hilt.navigation.compose.hiltViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object CurrentWeather : Screen("current_weather", "Current Weather", Icons.Default.Home)
    object WeatherHistory : Screen("weather_history", "Weather History", Icons.Default.List)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GWeatherNavigation(
    isAuthenticated: Boolean,
    onAuthStateChange: (Boolean) -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val screens = listOf(Screen.CurrentWeather, Screen.WeatherHistory)

    when {
        !isAuthenticated -> {
            AuthNavigation(
                onAuthSuccess = { onAuthStateChange(true) }
            )
        }
        else -> {
            MainNavigation(
                onSignOut = {
                    authViewModel.logout()
                    onAuthStateChange(false)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainNavigation(
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val screens = listOf(Screen.CurrentWeather, Screen.WeatherHistory)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF2D0B57)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = Color.White.copy(alpha = 0.15f),
                            unselectedIconColor = Color.White.copy(alpha = 0.5f),
                            unselectedTextColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.CurrentWeather.route,
        ) {
            composable(Screen.CurrentWeather.route) {
                CurrentWeatherScreen(
                    contentPadding = innerPadding,
                    onSignOut = onSignOut
                )
            }
            composable(Screen.WeatherHistory.route) {
                WeatherHistoryScreen(
                    contentPadding = innerPadding,
                    onSignOut = onSignOut
                )
            }
        }
    }
}

@Composable
private fun AuthNavigation(
    onAuthSuccess: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = onAuthSuccess,
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = onAuthSuccess,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}
