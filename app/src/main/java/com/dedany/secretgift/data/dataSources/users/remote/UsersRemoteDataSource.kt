package com.dedany.secretgift.data.dataSources.users.remote

import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto

interface UsersRemoteDataSource {
    suspend fun signUpUser(user: CreateUserDto): CreateUserDto
}