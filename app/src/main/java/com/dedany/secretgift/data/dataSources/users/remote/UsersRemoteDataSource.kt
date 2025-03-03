package com.dedany.secretgift.data.dataSources.users.remote

import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto

interface UsersRemoteDataSource {
    suspend fun signUpUser(user: CreateUserDto): CreateUserDto
    suspend fun getUserDetails(userId: String): PlayerDto
    suspend fun getIdUserByEmail(email: String): String
    suspend fun getUserById(userId: String): PlayerDto
}