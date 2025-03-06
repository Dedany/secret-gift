package com.dedany.secretgift.data.dataSources.games.remote

import com.dedany.secretgift.data.dataSources.games.remote.api.SecretGiftApi
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import javax.inject.Inject

class GameRemoteDataSourceImpl @Inject constructor(
    private val gamesApi: SecretGiftApi,
    private val userPreferences: UserPreferences
) : GameRemoteDataSource {

    override suspend fun getGamesByUser(): List<GameDto> {

        val userId = userPreferences.getUserId()

        if (userId.isEmpty()) {
            throw Exception("User ID not found in preferences")
        }
        val response = gamesApi.getGamesByUser(userId)

        if (response.isSuccessful) {
            return response.body()?.data ?: emptyList()
        } else {
            throw Exception("Error fetching games by user: ${response.errorBody()?.string()}")
        }
    }
}

