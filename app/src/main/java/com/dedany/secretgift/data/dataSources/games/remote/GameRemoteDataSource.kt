package com.dedany.secretgift.data.dataSources.games.remote

import com.dedany.secretgift.data.dataSources.games.remote.dto.CreateGameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto

interface GameRemoteDataSource {
    suspend fun getGame(gameCode: String): GameDto
    suspend fun getGamesByUser(userId: String): List<GameDto>
    suspend fun createGame(game: CreateGameDto):Boolean
    suspend fun deleteGame(game: GameDto)
    suspend fun updateGame(game: GameDto)


}