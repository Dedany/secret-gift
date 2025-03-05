package com.dedany.secretgift.data.repositories

import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.data.dataSources.users.remote.UsersRemoteDataSource
import com.dedany.secretgift.domain.entities.RegisteredUser
import com.dedany.secretgift.domain.repositories.UsersRepository
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val usersRemoteDataSource: UsersRemoteDataSource,
    private val userPreferences: UserPreferences
) : UsersRepository {

    override suspend fun getRegisteredUser(): RegisteredUser {
        val userId = userPreferences.getUserId()

        if (userId.isNotEmpty()) {
            val registeredUser = usersRemoteDataSource.getUserById(userId)

            return registeredUser.toLocal()
        } else {
            throw Exception("User not found")
        }
    }

    private fun PlayerDto.toLocal(): RegisteredUser {
        return RegisteredUser(
            id = this.id,
            email = this.email,
            name = this.name,
        )
    }
}
