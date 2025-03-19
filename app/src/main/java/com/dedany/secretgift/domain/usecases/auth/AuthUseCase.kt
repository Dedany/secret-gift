package com.dedany.secretgift.domain.usecases.auth

interface AuthUseCase {
    suspend fun login(email: String, password: String): Boolean
    suspend fun register(
        name: String,
        email: String,
        password: String,
    ): Boolean

    fun isEmailFormatValid(email: String): Boolean
    fun isPasswordFormatValid(password: String): Boolean
    fun isLoginFormValid(email: String, password: String): Boolean
    fun isNameValid(name: String): Boolean
    fun isPasswordMatching(value: String, repeatPassword: String): Boolean
    fun isRegisterFormValid(
        name: String,
        email: String,
        password: String,
        repeatPassword: String,
        termsAndConditions: Boolean
    ): Boolean
    suspend fun logout(): Boolean

    suspend fun isLoggedIn (): Boolean
}