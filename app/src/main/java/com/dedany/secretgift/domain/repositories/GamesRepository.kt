package com.dedany.secretgift.domain.repositories

import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Game

interface GamesRepository {
    suspend fun getGame(gameCode: String): Game
    suspend fun getLocalGame(gameId: Int): LocalGame
    suspend fun getGamesByUser(): List<Game>
    suspend fun deleteLocalGame(game: LocalGame)
    suspend fun createLocalGame(game: LocalGame)
    suspend fun updateLocalGame(game: LocalGame)
    suspend fun createGame(game: Game)
    suspend fun updateGame(game: Game)
    suspend fun deleteGame(game: Game)
}