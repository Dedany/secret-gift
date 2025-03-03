package com.dedany.secretgift.data.respositories

import com.dedany.secretgift.data.dataSources.auth.remote.AuthRemoteDataSource
import com.dedany.secretgift.data.dataSources.auth.remote.dto.LoginDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.data.dataSources.users.remote.UsersRemoteDataSource
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import com.dedany.secretgift.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val userPreferences: UserPreferences,
    private val usersRemoteDataSource: UsersRemoteDataSource
) :
    AuthRepository {
    override suspend fun login(email: String,password: String): Boolean {
        val credentials = LoginDto(email, password,null)
        val dto = authRemoteDataSource.login(credentials)
        if (!dto.token.isNullOrEmpty()) {
            val userId = usersRemoteDataSource.getIdUserByEmail(email)
            userPreferences.setUserId(userId)
            return true
        }
        return false
    }

    override suspend fun register(name: String, email: String, password: String): Boolean {
        val dto = CreateUserDto(
            name = name,
            email = email,
            password = password
        )
        val responseDto = authRemoteDataSource.register(dto)
        return responseDto.first
    }

    override fun logout(): Boolean {
        return authRemoteDataSource.logout()
    }




}