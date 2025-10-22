package com.jeollantino.gweather.data.repository

import com.jeollantino.gweather.data.local.dao.UserDao
import com.jeollantino.gweather.model.AuthState
import com.jeollantino.gweather.model.LoginRequest
import com.jeollantino.gweather.model.RegisterRequest
import com.jeollantino.gweather.model.User
import com.jeollantino.gweather.security.SecurePreferencesManager
import com.jeollantino.gweather.security.SecurePreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val securePreferences: SecurePreferencesManager
) {
    private val _authState = MutableStateFlow(AuthState())
    val authState: Flow<AuthState> = _authState.asStateFlow()

    init {
        // Restore session from secure storage if available
        restoreSession()
    }

    suspend fun login(request: LoginRequest): Result<User> {
        return try {
            // Set loading state at the start
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val passwordHash = hashPassword(request.password)
            val user = userDao.getUserByCredentials(request.username, passwordHash)

            if (user != null) {
                val domainUser = user.toDomainModel()
                _authState.value = AuthState(
                    isAuthenticated = true,
                    currentUser = domainUser,
                    isLoading = false
                )
                // Save user session securely
                saveSession(domainUser)
                Result.success(domainUser)
            } else {
                _authState.value = AuthState(
                    error = "Invalid username or password",
                    isLoading = false
                )
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            _authState.value = AuthState(
                error = e.message,
                isLoading = false
            )
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<User> {
        return try {
            // Set loading state at the start
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            // Check if username already exists
            if (userDao.getUserByUsername(request.username) != null) {
                _authState.value = AuthState(
                    error = "Username already exists",
                    isLoading = false
                )
                return Result.failure(Exception("Username already exists"))
            }

            // Check if email already exists
            if (userDao.getUserByEmail(request.email) != null) {
                _authState.value = AuthState(
                    error = "Email already exists",
                    isLoading = false
                )
                return Result.failure(Exception("Email already exists"))
            }

            val passwordHash = hashPassword(request.password)
            val userEntity = com.jeollantino.gweather.data.local.entity.User(
                username = request.username,
                email = request.email,
                passwordHash = passwordHash
            )

            val userId = userDao.insertUser(userEntity)
            val user = userDao.getUserById(userId)

            if (user != null) {
                val domainUser = user.toDomainModel()
                _authState.value = AuthState(
                    isAuthenticated = true,
                    currentUser = domainUser,
                    isLoading = false
                )
                // Save user session securely
                saveSession(domainUser)
                Result.success(domainUser)
            } else {
                _authState.value = AuthState(
                    error = "Failed to create user",
                    isLoading = false
                )
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            _authState.value = AuthState(
                error = e.message,
                isLoading = false
            )
            Result.failure(e)
        }
    }

    suspend fun logout() {
        _authState.value = AuthState()
        // Clear secure session data
        clearSession()
    }

    suspend fun getCurrentUser(): User? {
        return _authState.value.currentUser
    }

    /**
     * Save user session securely using EncryptedSharedPreferences
     */
    private fun saveSession(user: User) {
        securePreferences.putLong(SecurePreferenceKeys.USER_ID, user.id)
        securePreferences.putLong(SecurePreferenceKeys.LAST_LOGIN_TIME, System.currentTimeMillis())
    }

    /**
     * Restore user session from secure storage
     */
    private fun restoreSession() {
        val userId = securePreferences.getLong(SecurePreferenceKeys.USER_ID, -1L)
        if (userId != -1L) {
            // Session exists - could restore user state here if needed
            // For now, we'll require re-login for security
        }
    }

    /**
     * Clear secure session data
     */
    private fun clearSession() {
        securePreferences.remove(SecurePreferenceKeys.USER_ID)
        securePreferences.remove(SecurePreferenceKeys.LAST_LOGIN_TIME)
    }

    fun isAuthenticated(): Boolean {
        return _authState.value.isAuthenticated
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun com.jeollantino.gweather.data.local.entity.User.toDomainModel(): User {
        return User(
            id = this.id,
            username = this.username,
            email = this.email,
            passwordHash = this.passwordHash,
            createdAt = this.createdAt
        )
    }
}
