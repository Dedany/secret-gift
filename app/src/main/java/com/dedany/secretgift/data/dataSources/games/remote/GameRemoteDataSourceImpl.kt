package com.dedany.secretgift.data.dataSources.games.remote

import com.dedany.secretgift.data.dataSources.errorHandler.ErrorDto
import com.dedany.secretgift.data.dataSources.games.remote.api.SecretGiftApi
import com.dedany.secretgift.data.dataSources.games.remote.dto.CreateGameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import java.lang.reflect.Type

import javax.inject.Inject

class GameRemoteDataSourceImpl @Inject constructor(
    private val gamesApi: SecretGiftApi,
    private val userPreferences: UserPreferences
):GameRemoteDataSource {
    override suspend fun getGame(gameCode: String): GameDto {
       val response = gamesApi.getGameByAccessCode(gameCode)
        if (response.isSuccessful) {
            return response.body()!!
        }
      else {
            val bodyError: ResponseBody? = response.errorBody()
            val type: Type = object : TypeToken<ErrorDto>() {}.type
            val errorDto: ErrorDto = Gson().fromJson(bodyError?.string(), type)
            throw errorDto
      }

    }


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

    override suspend fun createGame(game: CreateGameDto):Boolean {
       val response = gamesApi.createGame(game)
        if (response.isSuccessful) {
            return true
        } else {
            throw Exception("Error creando el juego: ${response.errorBody()?.string()}")

        }
    }

    override suspend fun deleteGame(game: GameDto) {
        TODO("Not yet implemented")
    }

    override suspend fun updateGame(game: GameDto) {
        TODO("Not yet implemented")
    }
}

