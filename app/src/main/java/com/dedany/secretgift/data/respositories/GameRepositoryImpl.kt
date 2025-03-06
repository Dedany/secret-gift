package com.dedany.secretgift.data.respositories

import android.util.Log
import com.dedany.secretgift.data.dataSources.games.local.GameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.GamesDao
import com.dedany.secretgift.data.dataSources.games.local.PlayerDbo
import com.dedany.secretgift.data.dataSources.games.remote.GameRemoteDataSource
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameRuleDto


import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.Player
import com.dedany.secretgift.domain.entities.RegisteredUser
import com.dedany.secretgift.domain.entities.Rule
import com.dedany.secretgift.domain.repositories.GamesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val remoteDataSource: GameRemoteDataSource,
    private val localGamesDataSource: GamesDao,
) : GamesRepository {

    override suspend fun getGame(gameCode: String): Game {
        return withContext(Dispatchers.IO) {
            val gameDto = remoteDataSource.getGame(gameCode)
            val game = gameDto.toDomain()
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

    override suspend fun deleteGame(game: Game) {
        val gameDbo = game.toGameDbo()
        localGamesDataSource.delete(gameDbo)
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
            players = this.players.map { it.toPlayerDbo() }

        )
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


    private fun Player.toPlayerDbo(): PlayerDbo {
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

    private fun PlayerDto.toDomain(): Player {
        return Player(
            id = this.id,
            name = this.name,
            email = this.email,
            playerCode = this.playerCode
        )
    }

    private fun GameRuleDto.toGameRule(): Rule {
        return Rule(
            player1 = this.player1,
            player2 = this.player2
        )
    }
}