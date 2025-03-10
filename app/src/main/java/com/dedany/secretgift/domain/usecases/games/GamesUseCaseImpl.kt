package com.dedany.secretgift.domain.usecases.games

import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.repositories.GamesRepository
import javax.inject.Inject

class GamesUseCaseImpl @Inject constructor(
    private val repository: GamesRepository,
) : GamesUseCase {
    override suspend fun getGame(gameCode: String): Game {
        val game = repository.getGame(gameCode)
        return game
    }

    override suspend fun getLocalGame(gameId: Int): LocalGame {
        return repository.getLocalGame(gameId)
    }


    override suspend fun getGamesByUser(): List<Game> {
        val games = repository.getGamesByUser()
        return games
    }

    override suspend fun deleteLocalGame(game: LocalGame) {
        repository.deleteLocalGame(game)
    }

    override suspend fun createLocalGame(game: LocalGame) {
        repository.createLocalGame(game)
    }

    override suspend fun updateLocalGame(game: LocalGame) {
        repository.updateLocalGame(game)
    }

    override suspend fun createGame(gameId: Int): Boolean {
        val localGame = getLocalGame(gameId)
        return true

    }

    override suspend fun updateGame(game: Game) {
        repository.updateGame(game)
    }

    override suspend fun deleteGame(game: Game) {
        repository.deleteGame(game)
    }


}