package com.dedany.secretgift.data.respositories

import android.database.sqlite.SQLiteException
import android.util.Log
import com.dedany.secretgift.data.dataSources.games.local.LocalDataSource
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.PlayerDbo
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.RuleDbo
import com.dedany.secretgift.data.dataSources.games.remote.GameRemoteDataSource
import com.dedany.secretgift.data.dataSources.games.remote.dto.CreateGameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.CreatePlayerDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameRuleDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameSummaryDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.SendEmailToPlayerDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.UserRegisteredDto
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.domain.entities.CreateGame
import com.dedany.secretgift.domain.entities.CreatePlayer
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.GameSummary
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Player
import com.dedany.secretgift.domain.entities.Rule
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.domain.repositories.GamesRepository
import com.dedany.secretgift.domain.repositories.errorHandler.GameRepositoryError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val remoteDataSource: GameRemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val userPreferences: UserPreferences
) : GamesRepository {

    override suspend fun getGame(gameCode: String): Game {
        return withContext(Dispatchers.IO) {

            try {
                val gameDto = remoteDataSource.getGame(gameCode)
                return@withContext gameDto.toDomain()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun getLocalGame(gameId: Int): LocalGame {
        return withContext(Dispatchers.IO) {
            try {
                val gameDbo = localDataSource.getLocalGame(gameId)
                gameDbo.toDomain()
            } catch (e: Exception) {
                throw handleRepositoryError(e)
            }
        }
    }

    override suspend fun getLocalGameById(id: Int): LocalGame {
        return withContext(Dispatchers.IO) {
            try {
                val gameDbo = localDataSource.getLocalGameById(id)
                if (gameDbo != null) {
                    return@withContext gameDbo.toDomain()
                } else {
                    throw GameRepositoryError.GameNotFoundError("Juego no encontrado con el id: $id")
                }
            } catch (e: Exception) {
                throw handleRepositoryError(e)
            }
        }
    }


    override suspend fun getOwnedGamesByUser(): List<GameSummary> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = userPreferences.getUserId()
                if (userId.isEmpty()) {
                    throw GameRepositoryError.UserNotFoundError()
                }
                val gamesSummaryDto = remoteDataSource.getOwnedGamesByUser(userId)
                gamesSummaryDto.map { it.toDomainAsOwned() }
            } catch (e: Exception) {
                throw handleRepositoryError(e)
            }
        }
    }

    override suspend fun getLocalGamesByUser(): List<LocalGame> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = userPreferences.getUserId()
                if (userId.isEmpty()) {
                    throw GameRepositoryError.UserNotFoundError()
                }
                val gamesDbo = localDataSource.getLocalGamesByUser(userId)
                return@withContext gamesDbo.map { it.toDomain() }
            } catch (e: Exception) {
                throw handleRepositoryError(e)
            }
        }
    }


    override suspend fun sendMailToPlayer(
        gameId: String,
        playerId: String,
        playerEmail: String
    ): Boolean {
        return try {
            val sendEmailToPlayerDto = SendEmailToPlayerDto(gameId, playerId, playerEmail)
            remoteDataSource.sendMailToPlayer(sendEmailToPlayerDto)
        } catch (e: Exception) {
            throw handleRepositoryError(e)
        }
    }

    override suspend fun getPlayedGamesByUser(): List<GameSummary> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = userPreferences.getUserId()
                if (userId.isEmpty()) {
                    throw GameRepositoryError.UserNotFoundError()
                }
                val gamesSummaryDto = remoteDataSource.getPlayedGamesByUser(userId)
                return@withContext gamesSummaryDto.map { it.toDomain() }
            } catch (e: Exception) {
                Log.e("getGamesByUser", "Error obteniendo juegos: ${e.message}")
                return@withContext emptyList<GameSummary>()
            }
        }
    }


    override suspend fun deleteAllGames(): Boolean {
        return try {
            localDataSource.deleteAllGames()
        } catch (e: Exception) {
            throw handleRepositoryError(e)
        }
    }

    override suspend fun deleteLocalGame(gameId: Int): Boolean {
        return try {
            localDataSource.deleteGame(gameId)
        } catch (e: Exception) {
            throw handleRepositoryError(e)
        }
    }

    override suspend fun createLocalGame(game: LocalGame): Long {
        return try {
            localDataSource.createGame(game.toDbo())
        } catch (e: Exception) {
            throw handleRepositoryError(e)
        }
    }

    override suspend fun updateLocalGame(game: LocalGame): Int {
        return try {
            localDataSource.updateGame(game.toDbo())
        } catch (e: Exception) {
            throw handleRepositoryError(e)
        }
    }

    override suspend fun createGame(game: CreateGame): Boolean {
        return try {
            val gameDto = game.toDto()
            remoteDataSource.createGame(gameDto)
        } catch (e: Exception) {
            throw handleRepositoryError(e)
        }
    }

    override suspend fun updateGame(game: Game) {
//        val gameDto = game.toDto()
//        remoteDataSource.updateGame(gameDto)
    }

    private fun handleRepositoryError(e: Exception): GameRepositoryError {
        return when (e) {
            is SQLiteException -> {
                GameRepositoryError.DatabaseError(e.message ?: "Error en la base de datos")
            }
            is GameRepositoryError.UserNotFoundError -> {
                e
            }
            else -> {
                GameRepositoryError.UnexpectedError(e.message ?: "Error inesperado")
            }
        }
    }


    override suspend fun deleteGame(gameId: String, userId: String): Boolean {
        return remoteDataSource.deleteGame(gameId, userId)
    }


    private fun GameDto.toDomain(): Game {
        return try {
            if (this.id.isEmpty() || this.name.isEmpty()) {
                throw GameRepositoryError.DataConversionError("Missing required fields in GameDto: id or name")
            }
            Game(
                id = this.id,
                name = this.name,
                ownerId = this.ownerId,
                status = this.status,
                gameCode = this.gameCode,
                maxCost = this.maxCost,
                minCost = this.minCost,
                gameDate = this.gameDate,
                players = this.players.map { it.toDomain() },
                currentPlayer = this.currentPlayer,
                matchedPlayer = this.matchedPlayer,
                rules = this.rules.map { it.toDomain() }
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting GameDto to Game: ${e.message}")
        }
    }

    private fun LocalGame.toDbo(): GameDbo {
        return try {
            GameDbo(
                id = this.id,
                name = this.name,
                ownerId = this.ownerId,
                maxCost = this.maxCost,
                minCost = this.minCost,
                gameDate = this.gameDate ?: Date(),
                players = this.players.map { it.toDbo() },
                rules = this.rules.map { it.toDbo() }
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting LocalGame to GameDbo: ${e.message}")
        }
    }

    private fun GameDbo.toDomain(): LocalGame {
        return try {
            LocalGame(
                id = this.id,
                name = this.name,
                ownerId = this.ownerId,
                maxCost = this.maxCost,
                minCost = this.minCost,
                gameDate = this.gameDate,
                players = this.players.map { it.toDomain() },
                rules = this.rules.map { it.toDomain() }
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting GameDbo to LocalGame: ${e.message}")
        }
    }

    private fun UserRegisteredDto.toRegisteredUser(): User {
        return try {
            if (this.userId.isEmpty() || this.name.isEmpty() || this.email.isEmpty()) {
                throw GameRepositoryError.DataConversionError("Missing required fields in UserRegisteredDto")
            }
            User(
                id = this.userId,
                name = this.name,
                email = this.email
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting UserRegisteredDto to User: ${e.message}")
        }
    }

    private fun Player.toDbo(): PlayerDbo {
        return try {
            PlayerDbo(
                name = this.name,
                email = this.email
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting Player to PlayerDbo: ${e.message}")
        }
    }

    private fun PlayerDbo.toDomain(): Player {
        return try {
            Player(
                name = this.name,
                email = this.email
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting PlayerDbo to Player: ${e.message}")
        }
    }

    private fun PlayerDto.toDomain(): User {
        return try {
            if (this.id.isEmpty() || this.name.isEmpty() || this.email.isEmpty()) {
                throw GameRepositoryError.DataConversionError("Missing required fields in PlayerDto")
            }
            User(
                id = this.id,
                name = this.name,
                email = this.email,
                mailStatus = this.mailStatus
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting PlayerDto to User: ${e.message}")
        }
    }

    private fun GameRuleDto.toDomain(): Rule {
        return try {
            if (this.playerOne.isEmpty() || this.playerTwo.isEmpty()) {
                throw GameRepositoryError.DataConversionError("Missing required fields in GameRuleDto")
            }
            Rule(
                playerOne = this.playerOne,
                playerTwo = this.playerTwo
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting GameRuleDto to Rule: ${e.message}")
        }
    }

    private fun RuleDbo.toDomain(): Rule {
        return try {
            if (this.playerOne.isEmpty() || this.playerTwo.isEmpty()) {
                throw GameRepositoryError.DataConversionError("Missing required fields in RuleDbo")
            }
            Rule(
                playerOne = this.playerOne,
                playerTwo = this.playerTwo
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting RuleDbo to Rule: ${e.message}")
        }
    }

    private fun Rule.toDto(): GameRuleDto {
        return try {
            GameRuleDto(
                playerOne = this.playerOne.toString(),
                playerTwo = this.playerTwo.toString()
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting Rule to GameRuleDto: ${e.message}")
        }
    }

    private fun Rule.toDbo(): RuleDbo {
        return try {
            RuleDbo(
                playerOne = this.playerOne.toString(),
                playerTwo = this.playerTwo.toString()
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting Rule to RuleDbo: ${e.message}")
        }
    }

    private fun CreateGame.toDto(): CreateGameDto {
        return try {
            CreateGameDto(
                name = this.name,
                ownerId = this.ownerId,
                status = this.status,
                maxCost = this.maxCost,
                minCost = this.minCost,
                gameDate = this.gameDate,
                players = this.players.map { it.toDto() },
                rules = this.rules.map { it.toDto() }
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting CreateGame to CreateGameDto: ${e.message}")
        }
    }

    private fun CreatePlayer.toDto(): CreatePlayerDto {
        return try {
            CreatePlayerDto(
                name = this.name,
                email = this.email,
                linkedTo = this.linkedTo
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting CreatePlayer to CreatePlayerDto: ${e.message}")
        }
    }


    private fun GameSummaryDto.toDomain(): GameSummary {
        return try {
            GameSummary(
                id = this.id,
                name = this.name,
                status = this.status,
                accessCode = this.accessCode,
                date = this.gameDate,
                isOwnedGame = false
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting GameSummaryDto to GameSummary: ${e.message}")
        }
    }

    private fun GameSummaryDto.toDomainAsOwned(): GameSummary {
        return try {
            GameSummary(
                id = this.id,
                name = this.name,
                status = this.status,
                accessCode = this.accessCode,
                date = this.gameDate,
                isOwnedGame = true
            )
        } catch (e: Exception) {
            throw GameRepositoryError.DataConversionError("Error converting GameSummaryDto to GameSummary: ${e.message}")
        }
    }

    private fun String.toDate(): Date? {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.parse(this)
        } catch (e: Exception) {
            null
        }
    }
}


