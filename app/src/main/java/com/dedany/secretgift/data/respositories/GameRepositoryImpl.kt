package com.dedany.secretgift.data.respositories

import android.util.Log
import com.dedany.secretgift.data.dataSources.games.local.GamesDao
import com.dedany.secretgift.data.dataSources.games.local.LocalDataSource
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.PlayerDbo
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.RuleDbo
import com.dedany.secretgift.data.dataSources.games.remote.GameRemoteDataSource
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameRuleDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.UserRegisteredDto
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.Player
import com.dedany.secretgift.domain.entities.Rule
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.domain.repositories.GamesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val remoteDataSource: GameRemoteDataSource,
    private val localDataSource: LocalDataSource,
) : GamesRepository {

    override suspend fun getGame(gameCode: String): Game {
        return withContext(Dispatchers.IO) {
            val gameDto = remoteDataSource.getGame(gameCode)
            val game = gameDto.toDomain()
            return@withContext game
        }
    }

    override suspend fun getLocalGame(gameId: Int): LocalGame {
        return withContext(Dispatchers.IO) {
            val gameDbo = localDataSource.getGame(gameId)
            val game = gameDbo.toDomain()
            return@withContext game
        }
    }


    override suspend fun getGamesByUser(): List<Game> {
        return withContext(Dispatchers.IO) {
            try {
                val gamesDto = remoteDataSource.getGamesByUser()
                return@withContext gamesDto.map { it.toDomain() }
            } catch (e: Exception) {
                Log.e("getGamesByUser", "Error obteniendo juegos: ${e.message}")
                return@withContext emptyList<Game>()
            }
        }
    }

    override suspend fun createLocalPlayer(player: Player) {
        val player = player.toDbo()
        localDataSource.createPlayer(player)
    }

    override suspend fun deleteLocalGame(game: LocalGame) {
        val gameDbo = game.toDbo()
        localDataSource.deleteGame(gameDbo)
    }

    override suspend fun createLocalGame(game: LocalGame) {
        val gameDbo = game.toDbo()
        localDataSource.createGame(gameDbo)
    }

    override suspend fun updateLocalGame(game: LocalGame) {
        val gameDbo = game.toDbo()
        localDataSource.updateGame(gameDbo)
    }


    override suspend fun createGame(game: Game) {
        val gameDto = game.toDto()
        remoteDataSource.createGame(gameDto)
    }

    override suspend fun updateGame(game: Game) {
        val gameDto = game.toDto()
        remoteDataSource.updateGame(gameDto)
    }

    override suspend fun deleteGame(game: Game) {
        val gameDto = game.toDto()
        remoteDataSource.deleteGame(gameDto)
    }


    private fun GameDto.toDomain(): Game {
        return Game(
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
            rules = this.rules.map { it.toDomain() },
        )
    }


    private fun LocalGame.toDbo(): GameDbo {
        return GameDbo(
            id = this.id,
            name = this.name,
            ownerId = this.ownerId,
            maxCost = this.maxCost,
            minCost = this.minCost,
            gameDate = this.gameDate,
            players = this.players.map { it.toDbo() },
            rules = this.rules.map { it.toDbo() }
        )


    }


    private fun GameDbo.toDomain(): LocalGame {
        return LocalGame(
            id = this.id,
            name = this.name,
            ownerId = this.ownerId,
            maxCost = this.maxCost,
            minCost = this.minCost,
            gameDate = this.gameDate,
            players = this.players.map { it.toDomain() },
            rules = this.rules.map { it.toDomain() }
        )
    }

    private fun UserRegisteredDto.toRegisteredUser(): User {
        return User(
            id = this.userId,
            name = this.name,
            email = this.email
        )
    }

    private fun Player.toDbo(): PlayerDbo {
        return PlayerDbo(
            name = this.name,
            email = this.email
        )
    }

    private fun Player.toDto(): PlayerDto {
        return PlayerDto(
            name = this.name,
            email = this.email,
        )
    }

    private fun PlayerDbo.toDomain(): Player {
        return Player(
            name = this.name,
            email = this.email
        )
    }

    private fun PlayerDto.toDomain(): Player {
        return Player(
            name = this.name,
            email = this.email
        )
    }


    private fun GameRuleDto.toDomain(): Rule {
        return Rule(
            playerOne = this.playerOne,
            playerTwo = this.playerTwo
        )
    }

    private fun RuleDbo.toDomain(): Rule {
        return Rule(
            playerOne = this.playerOne,
            playerTwo = this.playerTwo
        )
    }
    private fun Rule.toDto(): GameRuleDto {
        return GameRuleDto(
            playerOne = this.playerOne.toString(),
            playerTwo = this.playerTwo.toString()
        )
    }

    private fun Rule.toDbo(): RuleDbo {
        return RuleDbo(
            playerOne = this.playerOne.toString(),
            playerTwo = this.playerTwo.toString()
        )
    }

    private fun Game.toDto(): GameDto {
        return GameDto(
            id = this.id,
            name = this.name,
            ownerId = this.ownerId,
            status = this.status,
            gameCode = this.gameCode,
            maxCost = this.maxCost,
            minCost = this.minCost,
            gameDate = this.gameDate,
            players = this.players.map { it.toDto() },
            rules = this.rules.map { it.toDto() },
            matchedPlayer = this.matchedPlayer,
            currentPlayer = this.currentPlayer

        )}




}


