package com.dedany.secretgift.data.dataSources.games.local.GameDbo

import com.dedany.secretgift.data.dataSources.games.local.GamesDao
import com.dedany.secretgift.data.dataSources.games.local.LocalDataSource
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val gamesDao: GamesDao
): LocalDataSource {

    override suspend fun getGames(): List<GameDbo> {
        return gamesDao.getGames()
    }

    override suspend fun insertGames(games: List<GameDbo>) {
        TODO("Not yet implemented")
    }



    override suspend fun deleteGame(game: GameDbo) {
        gamesDao.delete(game)
    }
}

