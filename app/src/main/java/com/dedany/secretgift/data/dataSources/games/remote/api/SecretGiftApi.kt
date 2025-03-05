package com.dedany.secretgift.data.dataSources.games.remote.api

import com.dedany.secretgift.data.dataSources.games.remote.dto.GamesDataDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SecretGiftApi {

    @GET("user/rooms/{user_id}")
    suspend fun getGamesByUser(@Path("user_id") userId: String): Response<GamesDataDto>

}