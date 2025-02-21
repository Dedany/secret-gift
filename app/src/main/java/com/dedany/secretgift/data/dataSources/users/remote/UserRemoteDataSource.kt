package com.dedany.secretgift.data.dataSources.users.remote

import com.dedany.secretgift.data.dataSources.users.remote.dto.UserDto

interface UserRemoteDataSource {
    suspend fun getUser(): List<UserDto>
}