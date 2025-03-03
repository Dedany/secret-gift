package com.dedany.secretgift.data.respositories

import com.dedany.secretgift.data.dataSources.games.local.GameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.GameDbo.GamePlayerDbo
import com.dedany.secretgift.data.dataSources.games.local.GamesDao
import com.dedany.secretgift.data.dataSources.games.local.PlayerDbo
import com.dedany.secretgift.data.dataSources.games.remote.GameRemoteDataSource
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameRuleDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.RegisteredUser
import com.dedany.secretgift.domain.entities.Rule
import com.dedany.secretgift.domain.repositories.GamesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val remoteDataSource: GameRemoteDataSource,
    private val localGamesDataSource: GamesDao,
    private val userPreferences: UserPreferences
) : GamesRepository {

    override suspend fun getGames(): List<Game> {
        return withContext(Dispatchers.IO) {
            val gamesDto = remoteDataSource.getGames() // Obtener juegos desde la API
            val gamesDbo = gamesDto.map { it.toLocal() } // Convertir a entidad local
            val games = gamesDto.map { it.toGame() } // Convertir a dominio

            localGamesDataSource.saveAllGames(gamesDbo)
            return@withContext games
        }
    }


    override suspend fun getGamesByUser(): List<Game> {
        return withContext(Dispatchers.IO) {
            // Obtener el userId desde las SharedPreferences
            val userId = userPreferences.getUserId()

            // Verificar si el userId está presente en SharedPreferences
            if (userId.isEmpty()) {
                throw Exception("User ID not found in preferences")
            }

            // Obtener los juegos desde la API utilizando el userId
            val gamesDto = remoteDataSource.getGamesByUser() // Obtener juegos desde la API

            // Convertir los DTOs de juegos a objetos de base de datos local
            val gamesDbo = gamesDto.map { it.toLocal() }

            // Guardar los juegos en la base de datos local
            localGamesDataSource.saveAllGames(gamesDbo)

            // Convertir los juegos de base de datos a entidades de dominio y devolverlos
            return@withContext gamesDbo.map { it.toDomain() }
        }
    }


    override suspend fun deleteGame(game: Game) {
        val gameDbo = game.toGameDbo()
        localGamesDataSource.delete(gameDbo)
    }

    private fun GameDto.toLocal(): GameDbo {
        return GameDbo(
            id = this.id,
            name = this.name,
            ownerId = this.ownerId,
            status = this.status,
            gameCode = this.gameCode,
            maxCost = this.maxCost,
            minCost = this.minCost,
            gameDate = this.gameDate,
            players = this.players.map { it.toPlayerDbo() }
        )
    }

    private fun PlayerDto.toPlayerDbo(): PlayerDbo {
        return PlayerDbo(
            id = this.id,
            name = this.name,
            email = this.email,
        )
    }


    private fun GameDbo.toDomain(): Game {
        return Game(
            id = this.id,
            name = this.name,
            ownerId = this.ownerId,
            status = this.status,
            gameCode = this.gameCode,
            maxCost = this.maxCost,
            minCost = this.minCost,
            gameDate = this.gameDate,
            players = this.players.map { it.toRegisteredUser() },

        )
    }


    private fun Game.toGameDbo(): GameDbo {
        return GameDbo(
            id = this.id,
            name = this.name,
            ownerId = this.ownerId,
            maxCost = this.maxCost,
            minCost = this.minCost,
            status = this.status,
            gameCode = this.gameCode,
            gameDate = this.gameDate,
            players = this.players.map { it.toPlayerDbo() } // Conversión correcta
        )
    }

    private fun GameDto.toGame(): Game {
        return Game(
            id = this.id,
            name = this.name,
            ownerId = this.ownerId,
            status = this.status,
            gameCode = this.gameCode,
            maxCost = this.maxCost,
            minCost = this.minCost,
            gameDate = this.gameDate,
            players = this.players.map { it.toRegisteredUser() }, // Conversión correcta
            rules = this.rules.map { it.toGameRule() }
        )
    }

    private fun PlayerDto.toRegisteredUser(): RegisteredUser {
        return RegisteredUser(
            id = this.id,
            name = this.name,
            email = this.email
        )
    }

    private fun GameRuleDto.toGameRule(): Rule {
        return Rule(
            player1 = this.player1,
            player2 = this.player2
        )
    }

    private fun RegisteredUser.toPlayerDbo(): PlayerDbo {
        return PlayerDbo(
            id = this.id,
            name = this.name,
            email = this.email,
        )
    }

    private fun PlayerDbo.toRegisteredUser(): RegisteredUser {
        return RegisteredUser(
            id = this.id,
            name = this.name,
            email = this.email
        )
    }

    private suspend fun saveGame(game: GameDbo) {
        localGamesDataSource.insertGame(game) // Guarda el juego en la tabla de juegos
        val gamePlayers = game.players.map { player ->
            GamePlayerDbo(game.id, player.id) // Relación juego-jugador
        }
        localGamesDataSource.insertGamePlayers(gamePlayers) // Guarda las relaciones en la tabla intermedia
    }



}
