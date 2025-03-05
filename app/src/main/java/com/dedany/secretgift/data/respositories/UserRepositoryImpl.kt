package com.dedany.secretgift.data.repositories

import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.data.dataSources.users.remote.UsersRemoteDataSource
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserEmailDto
import com.dedany.secretgift.domain.entities.RegisteredUser
import com.dedany.secretgift.domain.repositories.UsersRepository
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val usersRemoteDataSource: UsersRemoteDataSource,
    private val userPreferences: UserPreferences
) : UsersRepository {

    override suspend fun getRegisteredUser(): RegisteredUser {
        val email = userPreferences.getUserEmail()

        if (email.isNotEmpty()) {
            val response = usersRemoteDataSource.getUserByEmail(email = UserEmailDto(email))
            if (response.isSuccessful) {
                val playerDto = response.body()
                if (playerDto != null) {
                    return playerDto.toLocal()
                } else {
                    throw Exception("No user data found in response")
                }
            } else {
                throw Exception("Error: ${response.message()}")
            }
        } else {
            throw Exception("User not found")
        }
    }


    private fun PlayerDto.toLocal(): RegisteredUser {
        return RegisteredUser(
            id = this.userId,
            email = this.email,
            name = this.name
        )
    }
}
