package com.dedany.secretgift.data.dataSources.games.remote.api

import com.dedany.secretgift.data.dataSources.games.remote.dto.CreateGameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GamesDataDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SecretGiftApi {

    @GET("user/rooms/{user_id}")
    suspend fun getGamesByUser(@Path("user_id") userId: String): Response<GamesDataDto>

   @POST("/room/create")
   suspend fun createGame(@Body request: CreateGameDto): Response<GameDto>

}