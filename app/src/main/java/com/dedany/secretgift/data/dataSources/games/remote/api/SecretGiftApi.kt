package com.dedany.secretgift.data.dataSources.games.remote.api

import com.dedany.secretgift.data.dataSources.games.remote.dto.GamesDataDto
import retrofit2.http.GET

interface SecretGiftApi {

    @GET("games")
    suspend fun getGames(): List<GamesDataDto>
}