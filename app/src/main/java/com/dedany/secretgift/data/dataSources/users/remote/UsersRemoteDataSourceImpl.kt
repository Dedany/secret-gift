package com.dedany.secretgift.data.dataSources.users.remote

import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.users.api.UsersApi
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import javax.inject.Inject

class UsersRemoteDataSourceImpl @Inject constructor(
    private val usersApi: UsersApi
): UsersRemoteDataSource{
    override suspend fun signUpUser(user: CreateUserDto): CreateUserDto {
        return usersApi.signUpUser(user)
    }
    override suspend fun getUserDetails(userId: String): PlayerDto {
        return usersApi.getUserDetails()
    }
    override suspend fun getUserById(userId: String): PlayerDto {
        return usersApi.getUserById(userId)
    }
    override suspend fun getIdUserByEmail(email: String): String {
        return usersApi.getIdUserByEmail(email)
    }

}