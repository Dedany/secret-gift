package com.dedany.secretgift.data.dataSources.users.remote

import com.dedany.secretgift.data.dataSources.games.remote.dto.UserRegisteredDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserEmailDto
import retrofit2.Response

interface UsersRemoteDataSource {
    suspend fun signUpUser(user: CreateUserDto): CreateUserDto
    suspend fun getUserByEmail(email: UserEmailDto): Response<UserRegisteredDto>
}