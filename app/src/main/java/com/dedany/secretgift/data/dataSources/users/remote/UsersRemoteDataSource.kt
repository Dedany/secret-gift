package com.dedany.secretgift.data.dataSources.users.remote

import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import retrofit2.Response

interface UsersRemoteDataSource {
    suspend fun signUpUser(user: CreateUserDto): CreateUserDto
    suspend fun getUserByEmail(email: String): Response<PlayerDto>
}