package com.dedany.secretgift.data.dataSources.games.users.remote

import com.dedany.secretgift.data.dataSources.games.users.remote.dto.UserDto

interface UserRemoteDataSource {
    suspend fun getUser(): List<UserDto>
}