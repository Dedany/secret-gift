package com.dedany.secretgift.data.dataSources.users.remote

import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.users.api.UsersApi
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import retrofit2.Response
import javax.inject.Inject

class UsersRemoteDataSourceImpl @Inject constructor(
    private val usersApi: UsersApi
): UsersRemoteDataSource{
    override suspend fun signUpUser(user: CreateUserDto): CreateUserDto {
        return usersApi.signUpUser(user)
    }

    override suspend fun getUserByEmail(email: String): Response<PlayerDto> {
        return usersApi.getUserByEmail(email)
    }


}