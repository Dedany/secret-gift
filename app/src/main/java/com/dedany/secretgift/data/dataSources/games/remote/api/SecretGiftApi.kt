package com.dedany.secretgift.data.dataSources.games.remote.api

import com.dedany.secretgift.data.dataSources.games.remote.dto.GamesDataDto
import retrofit2.Response
import retrofit2.http.GET

interface SecretGiftApi {

    @GET("room/getallrooms")
    suspend fun getGames(): Response<GamesDataDto>
}