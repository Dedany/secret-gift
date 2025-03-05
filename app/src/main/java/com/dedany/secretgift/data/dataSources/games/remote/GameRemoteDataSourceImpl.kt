package com.dedany.secretgift.data.dataSources.games.remote

import com.dedany.secretgift.data.dataSources.games.remote.api.SecretGiftApi
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import javax.inject.Inject

class GameRemoteDataSourceImpl @Inject constructor(
    private val gamesApi: SecretGiftApi,
    private val userPreferences: UserPreferences
) : GameRemoteDataSource {

    private suspend fun fetchGames(): List<GameDto> {
        val response = gamesApi.getGames()
        if (response.isSuccessful) {
            return response.body()?.data ?: emptyList()
        } else {
            throw Exception("Error fetching games: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getGames(): List<GameDto> {
        return fetchGames()
    }

    override suspend fun getGamesByUser(): List<GameDto> {
        val email = userPreferences.getUserEmail()
        if (email.isEmpty()) {
            throw Exception("User email not found in preferences")
        }

        val games = fetchGames()
        return games.filter { game ->
            game.players.any { it.email == email }
        }
    }
}
