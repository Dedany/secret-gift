package com.dedany.secretgift.domain.usecases.games


import com.dedany.secretgift.domain.entities.Game


interface GamesUseCase {

    suspend fun getGames(): List<com.dedany.secretgift.domain.entities.Game>
}