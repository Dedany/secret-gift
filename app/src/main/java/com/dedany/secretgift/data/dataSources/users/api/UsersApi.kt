package com.dedany.secretgift.data.dataSources.users.api

import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserDto

import retrofit2.http.Body
import retrofit2.http.POST

interface UsersApi {
    @POST("user/create")
    suspend fun signUpUser(@Body user: CreateUserDto): CreateUserDto
}