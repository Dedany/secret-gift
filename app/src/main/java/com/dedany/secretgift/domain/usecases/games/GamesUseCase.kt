package com.dedany.secretgift.domain.usecases.games

import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.GameSummary
import com.dedany.secretgift.domain.entities.Player

interface GamesUseCase {
    suspend fun getGame(gameCode: String): Game
    suspend fun getLocalGame(gameId: Int): LocalGame
    suspend fun getOwnedGamesByUser(): List<GameSummary>
    suspend fun getLocalGamesById(id: Int): LocalGame
    suspend fun deleteLocalGame(gameId:Int): Boolean
    suspend fun createLocalGame(game: LocalGame): Long
    suspend fun updateLocalGame(game: LocalGame) : Int
    suspend fun createGame(gameId: Int): Boolean
    suspend fun updateGame(game: Game)
    suspend fun deleteGame(gameId: String, userId: String):Boolean
    suspend fun getLocalGamesByUser(): List<LocalGame>
    suspend fun deleteAllGames(): Boolean
    suspend fun canAssignGift(participants: List<Player>, restrictions: Map<String, Set<String>>): Boolean
    suspend fun sendMailToPlayer(gameId: String, playerId: String, playerEmail: String):Boolean
    suspend fun getPlayedGamesByUser(): List<GameSummary>
}