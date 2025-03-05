package com.dedany.secretgift.domain.repositories

import com.dedany.secretgift.domain.entities.Game

interface GamesRepository {
    suspend fun getGame(gameCode: String): Game
    suspend fun getGames(): List<Game>
    suspend fun getGamesByUser(): List<Game>
    suspend fun deleteGame(game: Game)
}