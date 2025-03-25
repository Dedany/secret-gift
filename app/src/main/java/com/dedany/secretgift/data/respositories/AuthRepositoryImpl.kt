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
        if (email.isBlank() || password.isBlank()) {
            throw AuthRepositoryError.InvalidCredentialsError("Email or password cannot be empty")
        }
        val credentials = LoginDto(email, password, null)

        try {
            val response = authRemoteDataSource.login(credentials)

            // Validar que el token no esté vacío
            if (response.token.isNullOrEmpty()) {
                throw AuthRepositoryError.InvalidCredentialsError("Invalid credentials or empty token")
            }

            val userResponse = authRemoteDataSource.getUserByEmail(UserEmailDto(email))

            val user = userResponse.body()?.takeIf {
                it.userId.isNotEmpty() && it.name.isNotEmpty()
            }

            user?.let { playerDto ->
                userPreferences.apply {
                    setUserEmail(email)
                    setUserId(playerDto.userId)
                    setUserName(playerDto.name)
                }
                return true
            } ?: throw AuthRepositoryError.UserNotFoundError("User not found or incomplete user data in the response")

        } catch (e: AuthRepositoryError.InvalidCredentialsError) {
            throw e
        } catch (e: Exception) {
            throw AuthRepositoryError.UnexpectedError("Error de credenciales")
        }
    }

    override suspend fun register(name: String, email: String, password: String): Boolean {

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw AuthRepositoryError.InvalidCredentialsError("Name, email, and password cannot be empty")
        }

        val dto = CreateUserDto(name = name, email = email, password = password)

        return try {
            val responseDto = authRemoteDataSource.register(dto)

            if (responseDto.first) {
                return true
            } else {
                throw AuthRepositoryError.UserRegistrationError("User registration failed")
            }
        } catch (e: Exception) {
            throw AuthRepositoryError.UnexpectedError("Unexpected error: ${e.message}")
        }
    }


    override fun logout(): Boolean {
        return authRemoteDataSource.logout()
    }

    override fun isLoggedIn(): Boolean {
        return authRemoteDataSource.isLoggedIn()
    }

    override suspend fun sendResetPasswordEmail(email: String): Boolean {
       return authRemoteDataSource.sendResetPasswordEmail(email)
    }

}