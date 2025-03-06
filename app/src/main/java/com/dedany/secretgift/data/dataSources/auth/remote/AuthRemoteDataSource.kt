package com.dedany.secretgift.data.dataSources.auth.remote

import com.dedany.secretgift.data.dataSources.auth.remote.dto.LoginDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.UserRegisteredDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserEmailDto
import retrofit2.Response

interface AuthRemoteDataSource {
    suspend fun login(loginDto: LoginDto): LoginDto
    suspend fun register(userDto: CreateUserDto):  Pair<Boolean, String>
    suspend fun getUserByEmail(email: UserEmailDto): Response<UserRegisteredDto>
    fun logout(): Boolean

}