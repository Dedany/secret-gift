package com.dedany.secretgift.domain.repositories

import com.dedany.secretgift.domain.entities.CreateGame
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.Player
import com.dedany.secretgift.domain.entities.SavePlayer


interface GamesRepository {
    suspend fun getGame(gameCode: String): Game
    suspend fun getLocalGame(gameId: Int): LocalGame
    suspend fun getLocalGameById(id: Int): LocalGame
    suspend fun getGamesByUser(): List<Game>
    suspend fun deleteLocalGame(game: LocalGame)
    suspend fun createLocalGame(game: LocalGame): Long
    suspend fun updateLocalGame(game: LocalGame): Int
    suspend fun createGame(game: CreateGame):Boolean
    suspend fun saveGameToBackend(gameId: Int,ownerId:String, gameName: String, players: List<SavePlayer>, eventDate: String, numPlayers: String, maxPrice: String, incompatibilities: List<Pair<String, String>>): Boolean
    suspend fun updateGame(game: Game)
    suspend fun deleteGame(game: Game)
}