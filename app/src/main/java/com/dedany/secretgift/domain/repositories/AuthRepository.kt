package com.dedany.secretgift.domain.repositories

import com.dedany.secretgift.domain.entities.RegisteredUser
import com.google.firebase.auth.UserInfo

interface AuthRepository {
    suspend fun login(email: String, password: String): Boolean
    fun logout(): Boolean
    suspend fun register(id: String, name: String, email: String, password: String): Boolean
    suspend fun getUsers(): List<RegisteredUser>
}