package com.dedany.secretgift.domain.usecases.auth

import com.dedany.secretgift.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : AuthUseCase {
    override suspend fun login(email: String, password: String): Boolean {
        return repository.login(email, password)
    }

    override suspend fun register(name: String, email: String, password: String): Boolean {
        val result = repository.register(name, email, password)
        return result
    }

    override fun isEmailFormatValid(email: String): Boolean {
        if (email.isNullOrEmpty()) {
            return false
        }

        if (email.matches("[a-zA-Z0-9._-]+@[a-z._-]+\\.+[a-z]+".toRegex())) {
            return true
        }

        return false
    }

    override fun isPasswordFormatValid(password: String): Boolean {
        if (password.isNullOrEmpty()) {
            return false
        }

        if (password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[#%^*+=_¿¡?=.*\\[/:()&@?!]).{6,}$".toRegex())) {
            return true
        }

        return false
    }

    override fun isLoginFormValid(email: String, password: String): Boolean {
        return isEmailFormatValid(email) && isPasswordFormatValid(password)
    }

    override fun isNameValid(name: String): Boolean {
        return name.isNotBlank() && name.length <= 50
    }


    override fun isPasswordMatching(value: String, repeatPassword: String): Boolean {
        val passwordMatches = value == repeatPassword
        return passwordMatches
    }

    override fun isRegisterFormValid(
        name: String,
        email: String,
        password: String,
        repeatPassword: String,
        termsAndConditions: Boolean
    ): Boolean {
        return isNameValid(name) && isEmailFormatValid(email) &&
                isPasswordFormatValid(password) && isPasswordMatching(
            password,
            repeatPassword
        ) && termsAndConditions
    }


}