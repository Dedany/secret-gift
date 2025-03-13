package com.dedany.secretgift.domain.usecases.games

import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.Player

interface GamesUseCase {
    suspend fun getGame(gameCode: String): Game
    suspend fun getLocalGame(gameId: Int): LocalGame
    suspend fun getGamesByUser(): List<Game>
    suspend fun getLocalGamesById(id: Int): LocalGame
    suspend fun deleteLocalGame(game: LocalGame)
    suspend fun createLocalGame(game: LocalGame): Long
    suspend fun updateLocalGame(game: LocalGame) : Int
    suspend fun saveGameToBackend(gameId: Int,ownerId: String, gameName: String, players: List<Player>, eventDate: String, numPlayers: String, maxPrice: String, incompatibilities: List<Pair<String, String>>): Boolean
    suspend fun createGame(gameId: Int): Boolean
    suspend fun updateGame(game: Game)
    suspend fun deleteGame(game: Game)
}