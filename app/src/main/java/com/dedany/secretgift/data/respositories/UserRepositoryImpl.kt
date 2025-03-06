package com.dedany.secretgift.data.repositories

import com.dedany.secretgift.data.dataSources.games.remote.dto.UserRegisteredDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.data.dataSources.users.remote.UsersRemoteDataSource
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserEmailDto
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.domain.repositories.UsersRepository
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val usersRemoteDataSource: UsersRemoteDataSource,
    private val userPreferences: UserPreferences
) : UsersRepository {

    override suspend fun getRegisteredUser(): User {

        val email = userPreferences.getUserEmail().takeIf { it.isNotEmpty() }
            ?: throw Exception("User not found")

        val response = usersRemoteDataSource.getUserByEmail(UserEmailDto(email))

        return response.body()?.toLocal() ?: throw Exception("No user data found in response")
    }


    private fun UserRegisteredDto.toLocal(): User {
        return User(
            id = this.userId,
            email = this.email,
            name = this.name
        )
    }
}
