package com.dedany.secretgift.presentation.login

import com.dedany.secretgift.domain.usecases.auth.AuthUseCase
import com.dedany.secretgift.domain.usecases.users.UsersUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var authUseCase: AuthUseCase
    private lateinit var userUseCase: UsersUseCase
    private lateinit var loginViewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp(){
        Dispatchers.setMain(testDispatcher)
        authUseCase = mockk()
        userUseCase = mockk()
        loginViewModel = LoginViewModel(authUseCase, userUseCase)
    }

    @Test
    fun when_setEmail_is_called_should_update_canDoLogin() {
        //Given
        val email = "pepe@pepe.com"
        val pass = "Pepe123..."
        every { authUseCase.isLoginFormValid(email,pass) } returns true
        loginViewModel.setPassword(pass)

        testDispatcher.scheduler.advanceUntilIdle()
        //When
        loginViewModel.setEmail(email)

        //Then
        assertEquals(true,loginViewModel.canDoLogin.value)
        verify { authUseCase.isLoginFormValid(email,pass) }
    }

    @After
    fun tearDown(){
        unmockkAll()
        Dispatchers.resetMain()
    }
}
