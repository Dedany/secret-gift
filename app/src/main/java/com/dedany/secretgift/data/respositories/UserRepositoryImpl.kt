package com.dedany.secretgift.data.repositories

import com.dedany.secretgift.data.dataSources.games.remote.dto.UserRegisteredDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.data.dataSources.users.remote.UsersRemoteDataSource
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserEmailDto
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.domain.repositories.UsersRepository
import com.dedany.secretgift.domain.repositories.errorHandler.UserRepositoryError
import java.net.UnknownHostException
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val usersRemoteDataSource: UsersRemoteDataSource,
    private val userPreferences: UserPreferences
) : UsersRepository {

    override suspend fun getRegisteredUser(): User {

        val email = userPreferences.getUserEmail().takeIf { it.isNotEmpty() }
            ?: throw UserRepositoryError.UserNotFoundError("User email not found in preferences")

        return try {
            val response = usersRemoteDataSource.getUserByEmail(UserEmailDto(email))
            response.body()?.toLocal()
                ?: throw UserRepositoryError.UserNotFoundError("No user data found in response")
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException -> throw UserRepositoryError.NetworkError("Network error: Unable to reach server")
                else -> throw UserRepositoryError.UnexpectedError("Unexpected error: ${e.message}")
            }
        }
    }


    private fun UserRegisteredDto.toLocal(): User {
        return User(
            id = this.userId,
            email = this.email,
            name = this.name
        )
    }
}
