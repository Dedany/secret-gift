package com.dedany.secretgift.data.dataSources.auth.remote

import com.dedany.secretgift.data.dataSources.auth.remote.dto.LoginDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserDto

interface AuthRemoteDataSource {

    suspend fun login(loginDto: LoginDto): LoginDto
    suspend fun register(userDto: UserDto):  Pair<Boolean, String>
    fun logout(): Boolean
    suspend fun getUsers(): List<UserDto>
}