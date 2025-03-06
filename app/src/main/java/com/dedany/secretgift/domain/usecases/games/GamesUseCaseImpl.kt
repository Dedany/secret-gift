package com.dedany.secretgift.domain.usecases.games

import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.repositories.GamesRepository
import javax.inject.Inject

class GamesUseCaseImpl @Inject constructor(
    private val repository: GamesRepository,
) : GamesUseCase {

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

    override suspend fun createGame(game: Game) {
        repository.createGame(game)
    }

    override suspend fun updateGame(game: Game) {
        repository.updateGame(game)
    }

    override suspend fun deleteGame(game: Game) {
        repository.deleteGame(game)
    }


}