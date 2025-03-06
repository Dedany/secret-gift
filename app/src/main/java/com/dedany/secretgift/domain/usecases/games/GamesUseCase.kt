package com.dedany.secretgift.domain.usecases.games

import com.dedany.secretgift.domain.entities.Game

interface GamesUseCase {
    suspend fun getGame(gameCode: String): Game

    suspend fun getGamesByUser(): List<Game>
    suspend fun deleteGame(game: Game)
}