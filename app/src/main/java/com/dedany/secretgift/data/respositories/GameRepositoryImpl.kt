package com.dedany.secretgift.data.respositories

import com.dedany.secretgift.data.dataSources.games.local.GameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.GamesDao
import com.dedany.secretgift.data.dataSources.games.remote.GameRemoteDataSource
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.repositories.GamesRepository
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepositoryImpl {
    class GameRepositoryImpl @Inject constructor(
        private val remoteDataSource: GameRemoteDataSource,
        private val localCharactersDataSource: GamesDao
    ) : GamesRepository {

        override suspend fun getGames(): List<Game> {
            return withContext(Dispatchers.IO) {

                val gamesDto = remoteDataSource.getGames()

                val gamesDbo = gamesDto.map { it.toLocal() }
                val game = gamesDto.map { it.toGame() }


                localCharactersDataSource.saveAllGames(gamesDbo)


                return@withContext game
            }
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
                //players = this.players.map { it.toUser() }
            )
        }

        private fun GameDbo.toDomain(): GameDto {
            return GameDto(
                id = this.id,
                name = this.name,
                ownerId = this.ownerId,
                status = this.status,
                gameCode = this.gameCode,
                maxCost = this.maxCost,
                minCost = this.minCost,
                gameDate = this.gameDate
                //players = this.players.map { it }
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
            )
        }

    }

}