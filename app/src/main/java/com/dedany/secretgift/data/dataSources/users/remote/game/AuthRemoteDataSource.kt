package com.dedany.secretgift.data.dataSources.users.remote.game

import com.dedany.secretgift.data.dataSources.users.remote.game.dto.LoginDto
import com.dedany.secretgift.data.dataSources.users.remote.game.dto.UserDto

interface AuthRemoteDataSource {
    suspend fun login(loginDto: LoginDto): LoginDto
    fun logout(): Boolean
    suspend fun register(userDto: UserDto): Pair<Boolean, String>
    suspend fun getUsers(): List<UserDto>
}