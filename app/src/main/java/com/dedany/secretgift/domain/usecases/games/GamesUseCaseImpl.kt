package com.dedany.secretgift.domain.usecases.games

import com.dedany.secretgift.domain.repositories.GamesRepository
import com.dedany.secretgift.domain.usecases.games.GamesUseCase
import javax.inject.Inject

class GamesUseCaseImpl @Inject constructor(
    private val repository: GamesRepository,
) : GamesUseCase {
    override suspend fun getGames(): List<com.dedany.secretgift.domain.entities.Game> {
        val games = repository.getGames()
        return games
    }
}