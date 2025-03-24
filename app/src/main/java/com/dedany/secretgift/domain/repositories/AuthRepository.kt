package com.dedany.secretgift.domain.repositories

interface AuthRepository {

    suspend fun login(email: String, password: String): Boolean
    suspend fun register(name: String, email: String, password: String): Boolean
    fun logout(): Boolean
    fun isLoggedIn(): Boolean
    suspend fun sendResetPasswordEmail(email: String): Boolean
}