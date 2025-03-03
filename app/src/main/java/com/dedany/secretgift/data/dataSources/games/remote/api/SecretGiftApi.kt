package com.dedany.secretgift.data.dataSources.games.remote.api

import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GamesDataDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SecretGiftApi {

    @GET("room/getallrooms")
    suspend fun getGames(): Response<GamesDataDto>

    @GET("user/{userId}")
    suspend fun getGamesByUser(@Path("userId") userId: String): Response<List<GameDto>>
}