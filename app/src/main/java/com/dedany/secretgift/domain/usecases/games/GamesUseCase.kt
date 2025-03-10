package com.dedany.secretgift.domain.usecases.games

import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Game

interface GamesUseCase {
    suspend fun getGame(gameCode: String): Game
    suspend fun getLocalGame(gameId: Int): LocalGame
    suspend fun getGamesByUser(): List<Game>
    suspend fun deleteLocalGame(game: LocalGame)
    suspend fun createLocalGame(game: LocalGame)
    suspend fun updateLocalGame(game: LocalGame)
    suspend fun createGame(gameId: Int): Boolean
    suspend fun updateGame(game: Game)
    suspend fun deleteGame(game: Game)
}