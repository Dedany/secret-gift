package com.dedany.secretgift.data.dataSources.users.api

import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UsersApi {
    @POST("user/create")
    suspend fun signUpUser(@Body user: CreateUserDto): CreateUserDto
    @POST("user/login")
    suspend fun getUserByEmail(@Body email: String): Response<PlayerDto>

}