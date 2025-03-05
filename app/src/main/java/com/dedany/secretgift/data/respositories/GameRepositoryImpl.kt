package com.dedany.secretgift.data.respositories


import com.dedany.secretgift.data.dataSources.games.local.GameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.GamesDao
import com.dedany.secretgift.data.dataSources.games.remote.GameRemoteDataSource
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.PlayerDto
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.Player
import com.dedany.secretgift.domain.repositories.GamesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val remoteDataSource: GameRemoteDataSource,
    private val localGamesDataSource: GamesDao
) : GamesRepository {
    override suspend fun getGame(gameCode: String): Game {
        return withContext(Dispatchers.IO) {
            val gameDto = remoteDataSource.getGame(gameCode)
            val gameDbo = gameDto.toLocal()
            val game = gameDto.toDomain()
            return@withContext game
        }
    }

    override suspend fun getGames(): List<Game> {
        return withContext(Dispatchers.IO) {
            val gamesDto = remoteDataSource.getGames() // Obtener juegos desde la API
            val gamesDbo = gamesDto.map { it.toLocal() } // Convertir a entidad local
            val games = gamesDto.map { it.toDomain() } // Convertir a dominio

            localGamesDataSource.saveAllGames(gamesDbo)
            return@withContext games
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
            gameDate = this.gameDate
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
            players = emptyList(),
            currentPlayer = "",
            matchedPlayer = ""
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
            matchedPlayer = this.matchedPlayer

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
            gameDate = this.gameDate
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
}
