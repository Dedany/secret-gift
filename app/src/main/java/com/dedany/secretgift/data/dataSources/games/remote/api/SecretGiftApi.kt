package com.dedany.secretgift.data.dataSources.games.remote.api

import com.dedany.secretgift.data.dataSources.games.remote.dto.CreateGameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Path

interface SecretGiftApi {


    @GET("room/")
    suspend fun getGameByAccessCode(@Query("accessCode") accessCode: String): Response<GameDto>

   @POST("/room/create")
   suspend fun createGame(@Body request: CreateGameDto): Response<GameDto>

}