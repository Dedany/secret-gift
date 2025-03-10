package com.dedany.secretgift.data.dataSources.games.local

import androidx.room.Insert
import androidx.room.Update
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.GameDbo

interface LocalDataSource {

    suspend fun getGames(): List<GameDbo>
    suspend fun getGame(gameId: Int): GameDbo
    suspend fun deleteGame(game: GameDbo)
    suspend fun createGame(game: GameDbo)
    suspend fun updateGame(game: GameDbo)
}