package com.jeollantino.gweather.util

import android.util.Patterns

/**
 * Validation utilities for user input
 */
object ValidationUtils {

    /**
     * Validates email format using Android's Patterns utility
     * @param email The email address to validate
     * @return true if email format is valid, false otherwise
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validates username format and length
     * Requirements:
     * - At least 3 characters
     * - Only alphanumeric characters (letters and numbers)
     * - No special characters or spaces
     *
     * @param username The username to validate
     * @return true if username meets requirements, false otherwise
     */
    fun isValidUsername(username: String): Boolean {
        if (username.length < 3) return false
        return username.matches(Regex("^[a-zA-Z0-9]+$"))
    }

    /**
     * Validates password strength
     * Requirements:
     * - At least 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character
     *
     * @param password The password to validate
     * @return true if password meets requirements, false otherwise
     */
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false

        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }

    /**
     * Get detailed username validation error message
     * @param username The username to validate
     * @return Error message key or null if valid
     */
    fun getUsernameError(username: String): String? {
        return when {
            username.isBlank() -> null // Don't show error for empty field
            username.length < 3 -> "validation_username_too_short"
            !username.matches(Regex("^[a-zA-Z0-9]+$")) -> "validation_username_invalid"
            else -> null
        }
    }

    /**
     * Get detailed password validation error message
     * @param password The password to validate
     * @return Error message key or null if valid
     */
    fun getPasswordError(password: String): String? {
        return when {
            password.isBlank() -> null // Don't show error for empty field
            password.length < 8 -> "validation_password_too_short"
            !isValidPassword(password) -> "validation_password_requirements"
            else -> null
        }
    }

    /**
     * Get email validation error message
     * @param email The email to validate
     * @return Error message key or null if valid
     */
    fun getEmailError(email: String): String? {
        return when {
            email.isBlank() -> null // Don't show error for empty field
            !isValidEmail(email) -> "validation_email_invalid"
            else -> null
        }
    }
}
