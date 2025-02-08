package com.dedany.secretgift.data.respositories

import com.dedany.secretgift.data.dataSources.games.local.GameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.GamesDao
import com.dedany.secretgift.data.dataSources.games.remote.GameRemoteDataSource
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.repositories.GamesRepository
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
                val games = gamesDto.map { it.toDomain() }


                localCharactersDataSource.saveAllGames(gamesDbo)


                return@withContext games
            }
        }


        private fun GameDto.toLocal(): GameDbo {
            return GameDbo(
                id = this.id,
                name = this.name,
                ownerId = this.ownerId,
                averageCost = this.averageCost,
                status = this.status,
                roomCode = this.roomCode,
                players = this.players.map { it.toUser() }
            )
        }

        private fun GameDto.toDomain(): Game {
            return Game(
                id = this.id,
                name = this.name,
                ownerId = this.ownerId,
                averageCost = this.averageCost,
                status = this.status,
                roomCode = this.roomCode,
                players = this.players.map { it.toUser() }
            )
        }


        private fun GameDbo.toDomain(): Game {
            return Game(
                id = this.id,
                name = this.name,
                ownerId = this.ownerId,
                averageCost = this.averageCost,
                status = this.status,
                roomCode = this.roomCode,
                players = this.players
            )
        }

        // Conversión de UserDto a User
        fun UserDto.toUser(): User {
            return if (this.email != null) {
                // Si tiene un email, es un usuario registrado
                RegisteredUser(
                    id = this.id,
                    name = this.name,
                    email = this.email,
                    password = this.password ?: "", // Asumimos que la contraseña es opcional

                )
            } else {
                // Si no tiene un email, es un usuario invitado
                GuestUser(
                    id = this.id,
                    name = this.name,

                    )
            }
        }
    }

}