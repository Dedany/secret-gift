package com.dedany.secretgift.data.dataSources.games.local

import com.dedany.secretgift.data.dataSources.games.local.gameDbo.GameDbo

interface LocalDataSource {

    suspend fun getGames(): List<GameDbo>
    suspend fun deleteGame(game: GameDbo)
}