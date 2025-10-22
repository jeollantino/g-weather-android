package com.jeollantino.gweather.ui.viewmodel

import com.jeollantino.gweather.data.repository.AuthRepository
import com.jeollantino.gweather.model.AuthState
import com.jeollantino.gweather.model.LoginRequest
import com.jeollantino.gweather.model.RegisterRequest
import com.jeollantino.gweather.model.User
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var authRepository: AuthRepository
    private lateinit var authStateFlow: MutableStateFlow<AuthState>

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepository = mockk()
        authStateFlow = MutableStateFlow(AuthState())

        every { authRepository.authState } returns authStateFlow

        viewModel = AuthViewModel(authRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() {
        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isAuthenticated)
        assertNull(state.currentUser)
        assertNull(state.error)
    }

    @Test
    fun `uiState should reflect auth state from repository`() = runTest {
        // Given
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")
        val newAuthState = AuthState(
            isLoading = false,
            isAuthenticated = true,
            currentUser = user,
            error = null
        )

        // When
        authStateFlow.value = newAuthState
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isAuthenticated)
        assertEquals(user, state.currentUser)
        assertNull(state.error)
    }

    @Test
    fun `login should call repository with correct credentials`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        val expectedRequest = LoginRequest(username, password)
        val user = User(id = 1L, username = username, email = "test@example.com", passwordHash = "hash123")

        coEvery { authRepository.login(any()) } returns Result.success(user)

        // When
        viewModel.login(username, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            authRepository.login(
                match { request ->
                    request.username == expectedRequest.username &&
                    request.password == expectedRequest.password
                }
            )
        }
    }

    @Test
    fun `login should update state on success`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        val user = User(id = 1L, username = username, email = "test@example.com", passwordHash = "hash123")

        coEvery { authRepository.login(any()) } answers {
            authStateFlow.value = AuthState(
                isLoading = false,
                isAuthenticated = true,
                currentUser = user,
                error = null
            )
            Result.success(user)
        }

        // When
        viewModel.login(username, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.isAuthenticated)
        assertEquals(user, state.currentUser)
        assertNull(state.error)
    }

    @Test
    fun `login should update state with error on failure`() = runTest {
        // Given
        val username = "testuser"
        val password = "wrongpassword"
        val errorMessage = "Invalid credentials"

        coEvery { authRepository.login(any()) } answers {
            authStateFlow.value = AuthState(
                isLoading = false,
                isAuthenticated = false,
                currentUser = null,
                error = errorMessage
            )
            Result.failure(Exception(errorMessage))
        }

        // When
        viewModel.login(username, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isAuthenticated)
        assertNull(state.currentUser)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `register should call repository with correct data`() = runTest {
        // Given
        val username = "newuser"
        val email = "newuser@example.com"
        val password = "password123"
        val expectedRequest = RegisterRequest(username, email, password)
        val user = User(id = 1L, username = username, email = email, passwordHash = "hash123")

        coEvery { authRepository.register(any()) } returns Result.success(user)

        // When
        viewModel.register(username, email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            authRepository.register(
                match { request ->
                    request.username == expectedRequest.username &&
                    request.email == expectedRequest.email &&
                    request.password == expectedRequest.password
                }
            )
        }
    }

    @Test
    fun `register should update state on success`() = runTest {
        // Given
        val username = "newuser"
        val email = "newuser@example.com"
        val password = "password123"
        val user = User(id = 1L, username = username, email = email, passwordHash = "hash123")

        coEvery { authRepository.register(any()) } answers {
            authStateFlow.value = AuthState(
                isLoading = false,
                isAuthenticated = true,
                currentUser = user,
                error = null
            )
            Result.success(user)
        }

        // When
        viewModel.register(username, email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.isAuthenticated)
        assertEquals(user, state.currentUser)
        assertNull(state.error)
    }

    @Test
    fun `register should update state with error on failure`() = runTest {
        // Given
        val username = "existinguser"
        val email = "existing@example.com"
        val password = "password123"
        val errorMessage = "Username already exists"

        coEvery { authRepository.register(any()) } answers {
            authStateFlow.value = AuthState(
                isLoading = false,
                isAuthenticated = false,
                currentUser = null,
                error = errorMessage
            )
            Result.failure(Exception(errorMessage))
        }

        // When
        viewModel.register(username, email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isAuthenticated)
        assertNull(state.currentUser)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `logout should call repository logout`() = runTest {
        // Given
        coEvery { authRepository.logout() } returns Unit

        // When
        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { authRepository.logout() }
    }

    @Test
    fun `logout should update state to unauthenticated`() = runTest {
        // Given
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")
        authStateFlow.value = AuthState(
            isLoading = false,
            isAuthenticated = true,
            currentUser = user,
            error = null
        )
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { authRepository.logout() } answers {
            authStateFlow.value = AuthState(
                isLoading = false,
                isAuthenticated = false,
                currentUser = null,
                error = null
            )
        }

        // When
        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isAuthenticated)
        assertNull(state.currentUser)
        assertNull(state.error)
    }

    @Test
    fun `clearError should clear error from state`() = runTest {
        // Given
        authStateFlow.value = AuthState(
            isLoading = false,
            isAuthenticated = false,
            currentUser = null,
            error = "Some error"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        val state = viewModel.uiState.value
        assertNull(state.error)
    }

    @Test
    fun `clearError should not affect other state properties`() = runTest {
        // Given
        val user = User(id = 1L, username = "testuser", email = "test@example.com", passwordHash = "hash123")
        authStateFlow.value = AuthState(
            isLoading = true,
            isAuthenticated = true,
            currentUser = user,
            error = "Some error"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertTrue(state.isAuthenticated)
        assertEquals(user, state.currentUser)
        assertNull(state.error)
    }
}
