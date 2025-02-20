package com.dedany.secretgift.data.dataSources.users.remote

import com.dedany.secretgift.data.dataSources.games.users.remote.dto.UserDto

interface UsersRemoteDataSource {
    suspend fun getUsers(): List<UserDto>
}