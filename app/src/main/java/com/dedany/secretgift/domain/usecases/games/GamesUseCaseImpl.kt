package com.dedany.secretgift.domain.usecases.games

import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.repositories.GamesRepository
import javax.inject.Inject

class GamesUseCaseImpl @Inject constructor(
    private val repository: GamesRepository,
) : GamesUseCase {
    override suspend fun getGames(): List<Game> {
        val games = repository.getGames()
        return games
    }

    override suspend fun getGamesByUser(): List<Game> {
        val games = repository.getGamesByUser()
        return games
    }

    override suspend fun deleteGame(game: Game) {
        repository.deleteGame(game)
    }
}