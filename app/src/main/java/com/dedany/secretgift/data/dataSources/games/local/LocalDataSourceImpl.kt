package com.dedany.secretgift.data.dataSources.games.local

import com.dedany.secretgift.data.dataSources.games.local.gameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.PlayerDbo
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val gamesDao: GamesDao
) : LocalDataSource {

    override suspend fun getGames(): List<GameDbo> {
        return gamesDao.getGames()
    }

    override suspend fun getLocalGame(gameId: Int): GameDbo {
        return gamesDao.getGame(gameId)
    }

    override suspend fun deleteGame(game: GameDbo) {
        gamesDao.deleteLocalGame(game)
    }

    override suspend fun createGame(game: GameDbo) {
        gamesDao.createLocalGame(game)
    }

    override suspend fun updateGame(game: GameDbo) {
       gamesDao.updateLocalGame(game)
    }
}

