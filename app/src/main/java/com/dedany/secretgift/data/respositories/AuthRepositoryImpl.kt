package com.dedany.secretgift.data.respositories

import com.dedany.secretgift.data.dataSources.auth.remote.AuthRemoteDataSource
import com.dedany.secretgift.data.dataSources.auth.remote.dto.LoginDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserEmailDto
import com.dedany.secretgift.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val userPreferences: UserPreferences,
) :
    AuthRepository {
    override suspend fun login(email: String, password: String): Boolean {
        val credentials = LoginDto(email, password, null)
        val response = authRemoteDataSource.login(credentials)

        if (response.token.isNullOrEmpty()) return false

        val userResponse = authRemoteDataSource.getUserByEmail(UserEmailDto(email))

        userResponse.body()?.takeIf { it.userId.isNotEmpty() }?.let { playerDto ->
            userPreferences.apply {
                setUserEmail(email)
                setUserId(playerDto.userId)
                setUserName(playerDto.name)
            }
            return true
        } ?: return false
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

    override fun isLoggedIn(): Boolean {
        TODO("Not yet implemented")
    }

}