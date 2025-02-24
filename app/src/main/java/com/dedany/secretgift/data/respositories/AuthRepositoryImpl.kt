package com.dedany.secretgift.data.respositories

import com.dedany.secretgift.domain.entities.RegisteredUser
import com.dedany.secretgift.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authRemoteDataSource: AuthRemoteDataSource) :
    AuthRepository {
    override suspend fun login(email: String, password: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun logout(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun register(
        id: String,
        name: String,
        email: String,
        password: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers(): List<RegisteredUser> {
        TODO("Not yet implemented")
    }
}