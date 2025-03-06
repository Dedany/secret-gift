package com.dedany.secretgift.data.dataSources.games.local

import com.dedany.secretgift.data.dataSources.games.local.gameDbo.GameDbo
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val gamesDao: GamesDao
) : LocalDataSource {

    override suspend fun getGames(): List<GameDbo> {
        return gamesDao.getGames()
    }

    override suspend fun deleteGame(game: GameDbo) {
        gamesDao.delete(game)
    }

    override suspend fun createGame(game: GameDbo) {
        gamesDao.createGame(game)
    }

    override suspend fun updateGame(game: GameDbo) {
       gamesDao.updateGame(game)
    }
}

