package com.dedany.secretgift.domain.repositories

import com.dedany.secretgift.domain.entities.CreateGame
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.GameSummary


interface GamesRepository {
    suspend fun getGame(gameCode: String): Game
    suspend fun getLocalGame(gameId: Int): LocalGame
    suspend fun getLocalGameById(id: Int): LocalGame
    suspend fun getGamesByUser(): List<GameSummary>
    suspend fun deleteLocalGame(gameId: Int): Boolean
    suspend fun createLocalGame(game: LocalGame): Long
    suspend fun updateLocalGame(game: LocalGame): Int
    suspend fun createGame(game: CreateGame):Boolean
    suspend fun updateGame(game: Game)
    suspend fun deleteGame(gameId: String): Boolean
    suspend fun getLocalGamesByUser(): List<LocalGame>
    suspend fun deleteAllGames(): Boolean


}