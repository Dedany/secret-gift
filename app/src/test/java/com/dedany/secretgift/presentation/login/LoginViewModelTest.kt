package com.dedany.secretgift.presentation.login

import com.dedany.secretgift.domain.usecases.auth.AuthUseCase
import com.dedany.secretgift.domain.usecases.users.UsersUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.verify

class LoginViewModelTest {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var authUseCase: AuthUseCase
    private lateinit var usersUseCase: UsersUseCase

    @Before
    fun setup() {
        authUseCase = mockk()
        usersUseCase = mockk()
        loginViewModel = LoginViewModel(authUseCase, usersUseCase)
    }

    @Test
    fun when_setEmail_is_called_assign_value_to_email_variable() {
        val mail ="john.connor@resistance.com"
        val pass = "Pepe1234."
        every { loginViewModel.isLoginFormValid(mail, pass) } returns true
        every { loginViewModel.setEmail(mail) } returns Unit
        loginViewModel.setEmail(mail)

        verify(loginViewModel.setEmail(mail))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}