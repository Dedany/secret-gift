package com.dedany.secretgift.data.dataSources.games.remote

import com.dedany.secretgift.data.errorHandler.ErrorDto
import com.dedany.secretgift.data.errorHandler.NetworkErrorDto
import com.dedany.secretgift.data.dataSources.games.remote.api.SecretGiftApi
import com.dedany.secretgift.data.dataSources.games.remote.dto.CreateGameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameSummaryDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.SendEmailToPlayerDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import java.io.IOException
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class GameRemoteDataSourceImpl @Inject constructor(
    private val gamesApi: SecretGiftApi
) : GameRemoteDataSource {

    override suspend fun getGame(gameCode: String): GameDto {
        val response = gamesApi.getGameByAccessCode(gameCode)
        if (response.isSuccessful) {
            return response.body()!!
            } else {
                val bodyError: ResponseBody? = response.errorBody()
                val type: Type = object : TypeToken<ErrorDto>() {}.type
                val errorDto: ErrorDto = Gson().fromJson(bodyError?.string(), type)
                throw errorDto

            }

        }

    override suspend fun getOwnedGamesByUser(userId: String): List<GameSummaryDto> {
        val response = try {
            gamesApi.getGamesByUser(userId)
        } catch (e: Exception) {
            throw handleNetworkError(e)
        }

        return if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            throw handleError(response.errorBody())
        }
    }

    override suspend fun createGame(game: CreateGameDto): Boolean {
        val response = try {
            gamesApi.createGame(game)
        } catch (e: Exception) {
            throw handleNetworkError(e)
        }
        return if (response.isSuccessful) {
            true
        } else {
            throw handleError(response.errorBody())
        }
    }

    override suspend fun deleteGame(gameId: String, userId: String): Boolean {
        val response = try {
            gamesApi.deleteGame(gameId, userId)
        } catch (e: Exception) {
            throw handleNetworkError(e)
        }

        return if (response.isSuccessful) {
            true
        } else {
            throw handleError(response.errorBody())
        }
    }

    override suspend fun updateGame(game: GameDto) {
    }

    override suspend fun sendMailToPlayer(
        sendEmailToPlayerDto: SendEmailToPlayerDto
    ): Boolean {
        val response = try {
            gamesApi.sendMailToPlayer(sendEmailToPlayerDto)
        } catch (e: Exception) {
            throw handleNetworkError(e)
        }

        return if (response.isSuccessful) {
            true
        } else {
            throw handleError(response.errorBody())
        }
    }

    override suspend fun getPlayedGamesByUser(userId: String): List<GameSummaryDto> {
        val response = try {
            gamesApi.getPlayedGamesByUser(userId)
        } catch (e: Exception) {
            throw handleNetworkError(e)
        }

        return if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            throw handleError(response.errorBody())
        }
    }

    private fun handleNetworkError(e: Exception): NetworkErrorDto {
        return when (e) {
            is UnknownHostException -> NetworkErrorDto.NoInternetConnection
            is SocketTimeoutException -> NetworkErrorDto.TimeOutError
            is IOException -> NetworkErrorDto.NoInternetConnection
            else -> NetworkErrorDto.UnknownErrorDto
        }
    }

    private fun handleError(errorBody: ResponseBody?): NetworkErrorDto {
        return try {
            val type: Type = object : TypeToken<ErrorDto>() {}.type
            val errorDto: ErrorDto = Gson().fromJson(errorBody?.charStream(), type)
            NetworkErrorDto.FailureError(400, errorDto.message ?: "Unknown error")
        } catch (e: Exception) {
            NetworkErrorDto.UnknownErrorDto
        }
    }
}


