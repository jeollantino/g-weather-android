package com.jeollantino.gweather.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeollantino.gweather.R
import com.jeollantino.gweather.model.AuthState
import com.jeollantino.gweather.ui.components.WeatherGradientBackground
import com.jeollantino.gweather.ui.viewmodel.AuthViewModel
import com.jeollantino.gweather.util.ValidationUtils

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
        }
    }

    val isFormValid = username.isNotBlank() &&
                      password.isNotBlank() &&
                      ValidationUtils.isValidUsername(username) &&
                      ValidationUtils.isValidPassword(password)

    LoginContent(
        uiState = uiState,
        onUsernameChange = {
            username = it
            usernameError = ValidationUtils.getUsernameError(it)
            viewModel.clearError()
        },
        onPasswordChange = {
            password = it
            passwordError = ValidationUtils.getPasswordError(it)
            viewModel.clearError()
        },
        onLoginClick = {
            usernameError = ValidationUtils.getUsernameError(username)
            passwordError = ValidationUtils.getPasswordError(password)

            if (isFormValid) {
                viewModel.login(username, password)
            }
        },
        onNavigateToRegister = onNavigateToRegister,
        onTogglePasswordVisibility = { showPassword = !showPassword },
        isPasswordVisible = showPassword,
        usernameValue = username,
        passwordValue = password,
        usernameError = usernameError,
        passwordError = passwordError,
        isFormValid = isFormValid
    )
}

@Composable
fun LoginContent(
    uiState: AuthState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    isPasswordVisible: Boolean,
    usernameValue: String,
    passwordValue: String,
    usernameError: String? = null,
    passwordError: String? = null,
    isFormValid: Boolean = false
) {
    WeatherGradientBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = usernameValue,
                onValueChange = onUsernameChange,
                label = { Text(stringResource(R.string.username), color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                isError = usernameError != null,
                supportingText = usernameError?.let {
                    {
                        Text(
                            text = stringResource(
                                when (it) {
                                    "validation_username_too_short" -> R.string.validation_username_too_short
                                    "validation_username_invalid" -> R.string.validation_username_invalid
                                    else -> R.string.validation_username_too_short
                                }
                            ),
                            color = Color(0xFFFF6B6B)
                        )
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    errorBorderColor = Color(0xFFFF6B6B),
                    errorTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordValue,
                onValueChange = onPasswordChange,
                label = { Text(stringResource(R.string.password), color = Color.White.copy(alpha = 0.7f)) },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null || uiState.error != null,
                supportingText = when {
                    passwordError != null -> {
                        {
                            Text(
                                text = stringResource(
                                    when (passwordError) {
                                        "validation_password_too_short" -> R.string.validation_password_too_short
                                        "validation_password_requirements" -> R.string.validation_password_requirements
                                        else -> R.string.validation_password_too_short
                                    }
                                ),
                                color = Color(0xFFFF6B6B)
                            )
                        }
                    }
                    uiState.error != null -> {
                        { Text(uiState.error, color = Color(0xFFFF6B6B)) }
                    }
                    else -> null
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    errorBorderColor = Color(0xFFFF6B6B),
                    errorTextColor = Color.White
                ),
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = stringResource(if (isPasswordVisible) R.string.hide_password else R.string.show_password),
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLoginClick,
                enabled = isFormValid && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF2D0B57),
                    disabledContainerColor = Color.White.copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp,
                    disabledElevation = 0.dp
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF2D0B57),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.login),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(stringResource(R.string.dont_have_account), color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginContent(
        uiState = AuthState(),
        onUsernameChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onNavigateToRegister = {},
        onTogglePasswordVisibility = {},
        isPasswordVisible = false,
        usernameValue = "testuser",
        passwordValue = "Password123!",
        usernameError = null,
        passwordError = null,
        isFormValid = true
    )
}
