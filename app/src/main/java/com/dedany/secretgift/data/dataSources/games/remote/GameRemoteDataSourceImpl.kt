package com.dedany.secretgift.data.dataSources.games.remote

import com.dedany.secretgift.data.dataSources.games.remote.api.SecretGiftApi
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import javax.inject.Inject

class GameRemoteDataSourceImpl @Inject constructor(
    private val gamesApi: SecretGiftApi,
    private val userPreferences: UserPreferences
):GameRemoteDataSource {
    override suspend fun getGame(gameCode: String): GameDto {
       val game = gamesApi.getGameByAccessCode(gameCode).body()
        return game!!
    }


    override suspend fun getGames(): List<GameDto> {
        val response = gamesApi.getGames()
        return if (response.isSuccessful) {
            response.body()?.data ?: emptyList() // Si el cuerpo es nulo, devuelve lista vacía
        } else {
            throw Exception("Error fetching games: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getGamesByUser(): List<GameDto> {

        val userId = userPreferences.getUserId()

        if (userId.isEmpty()) {
            throw Exception("User ID not found in preferences")
        }

        val response = gamesApi.getGamesByUser(userId)
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            throw Exception("Error fetching games for user $userId: ${response.errorBody()?.string()}")
        }
    }
}
