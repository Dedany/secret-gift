package com.dedany.secretgift.domain.repositories

import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Game

interface GamesRepository {

    suspend fun getGamesByUser(): List<Game>
    suspend fun deleteGame(game: LocalGame)
}