package com.dedany.secretgift.data.dataSources.games.local

import com.dedany.secretgift.data.dataSources.games.local.gameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.PlayerDbo

interface LocalDataSource {

    suspend fun getGames(): List<GameDbo>
    suspend fun getLocalGame(gameId: Int): GameDbo
    suspend fun deleteGame(game: GameDbo)
    suspend fun createGame(game: GameDbo)
    suspend fun updateGame(game: GameDbo)
}