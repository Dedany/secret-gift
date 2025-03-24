package com.dedany.secretgift.data.respositories

import com.dedany.secretgift.data.dataSources.auth.remote.AuthRemoteDataSource
import com.dedany.secretgift.data.dataSources.auth.remote.dto.LoginDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserEmailDto
import com.dedany.secretgift.domain.repositories.AuthRepository
import com.dedany.secretgift.domain.repositories.errorHandler.AuthRepositoryError
import java.net.UnknownHostException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val userPreferences: UserPreferences,
) :
    AuthRepository {
    override suspend fun login(email: String, password: String): Boolean {
        val credentials = LoginDto(email, password, null)
        try {
            val response = authRemoteDataSource.login(credentials)

            if (response.token.isNullOrEmpty()) {
                throw AuthRepositoryError.InvalidCredentialsError("Invalid credentials or empty token")
            }

            val userResponse = authRemoteDataSource.getUserByEmail(UserEmailDto(email))
            val user = userResponse.body()?.takeIf { it.userId.isNotEmpty() }

            user?.let { playerDto ->
                userPreferences.apply {
                    setUserEmail(email)
                    setUserId(playerDto.userId)
                    setUserName(playerDto.name)
                }
                return true
            } ?: throw AuthRepositoryError.UserNotFoundError("User not found in the response")
        } catch (e: java.net.UnknownHostException) {
            throw AuthRepositoryError.NetworkError("Network error: Unable to reach server")
        } catch (e: Exception) {
            throw AuthRepositoryError.UnexpectedError("Unexpected error: ${e.message}")
        }
    }

    override suspend fun register(name: String, email: String, password: String): Boolean {
        val dto = CreateUserDto(name = name, email = email, password = password)
        return try {
            val responseDto = authRemoteDataSource.register(dto)
            responseDto.first
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException -> throw AuthRepositoryError.NetworkError("Network error: Unable to reach server")
                else -> throw AuthRepositoryError.UnexpectedError("Unexpected error: ${e.message}")
            }
        }
    }

    override fun logout(): Boolean {
        return authRemoteDataSource.logout()
    }

    override fun isLoggedIn(): Boolean {
       return authRemoteDataSource.isLoggedIn()
    }

}