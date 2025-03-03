package com.dedany.secretgift.data.dataSources.users.remote

import com.dedany.secretgift.data.dataSources.users.api.UsersApi
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import javax.inject.Inject

class UsersRemoteDataSourceImpl @Inject constructor(
    private val usersApi: UsersApi
): UsersRemoteDataSource{
    override suspend fun signUpUser(user: CreateUserDto): CreateUserDto {
        return usersApi.signUpUser(user)
    }
}