package com.dedany.secretgift.data.dataSources.games.remote.api

import com.dedany.secretgift.data.dataSources.games.remote.dto.CreateGameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GamesDataDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.OwnedGamesSummaryDataDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayedGamesSummaryDataDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.SendEmailToPlayerDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.ServerResponse


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Path

interface SecretGiftApi {

    @GET("user/owned/{user_id}")
    suspend fun getGamesByUser(@Path("user_id") userId: String): Response<OwnedGamesSummaryDataDto>

    @GET("user/played/{user_id}")
    suspend fun getPlayedGamesByUser(@Path("user_id") userId: String): Response<PlayedGamesSummaryDataDto>

    @GET("room/")
    suspend fun getGameByAccessCode(@Query("accessCode") accessCode: String): Response<GameDto>

    @POST("/room/create")
    suspend fun createGame(@Body request: CreateGameDto): Response<GameDto>

    @POST("/room/sendemail")
    suspend fun sendMailToPlayer(
        @Body request: SendEmailToPlayerDto
    ): Response<ServerResponse>

    @DELETE("/room/{gameId}")
    suspend fun deleteGame(
        @Path("gameId") gameId: String,
        @Query("userId") userId: String
    ): Response<ServerResponse>


}