package com.dedany.secretgift.data.respositories

import android.util.Log
import com.dedany.secretgift.data.dataSources.auth.remote.AuthRemoteDataSource
import com.dedany.secretgift.data.dataSources.auth.remote.dto.LoginDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.data.dataSources.users.remote.UsersRemoteDataSource
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

        if (response.token.isNullOrEmpty()) {
            Log.e("AuthRepository", "Login failed: token is empty")
            return false
        }

        val userEmailDto = UserEmailDto(email)
        val userResponse = authRemoteDataSource.getUserByEmail(userEmailDto)

        if (userResponse.isSuccessful) {
            val playerDto = userResponse.body()

            Log.d("AuthRepository", "User fetched: $playerDto")

            if (playerDto?.userId.isNullOrEmpty()) {
                Log.e("AuthRepository", "User ID is null or empty")
                throw Exception("User ID is null or empty")
            }

            // Guardar en preferencias
            userPreferences.setUserEmail(email)
            userPreferences.setUserId(playerDto!!.userId)

            Log.d("AuthRepository", "User ID saved: ${playerDto.userId}")

            return true
        } else {
            val errorMessage = userResponse.errorBody()?.string()
            Log.e("AuthRepository", "Error fetching user by email: $errorMessage")
            throw Exception("Error fetching user by email: $errorMessage")
        }
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