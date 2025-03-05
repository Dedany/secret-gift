package com.dedany.secretgift.data.dataSources.users.api

import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserDto
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsersApi {
    @POST("user/create")
    suspend fun signUpUser(@Body user: CreateUserDto): CreateUserDto

    @GET("users/email/{email}")
    suspend fun getIdUserByEmail(@Path("email") email: String): String

    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): PlayerDto
}