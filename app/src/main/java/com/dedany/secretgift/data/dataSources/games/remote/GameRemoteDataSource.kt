package com.dedany.secretgift.data.dataSources.games.remote

import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto

interface GameRemoteDataSource {
    suspend fun getGame(gameCode: String): GameDto
    suspend fun getGames(): List<GameDto>
    suspend fun getGamesByUser(): List<GameDto>
}