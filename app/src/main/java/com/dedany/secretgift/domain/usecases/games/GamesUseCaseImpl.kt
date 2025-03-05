package com.dedany.secretgift.domain.usecases.games

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

    override suspend fun getGames(): List<Game> {
        val games = repository.getGames()
        return games
    }

    override suspend fun deleteGame(game: Game) {
        repository.deleteGame(game)
    }
}