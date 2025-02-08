package com.dedany.secretgift.domain.repositories

import com.dedany.secretgift.domain.entities.Game

interface GamesRepository {

    suspend fun getGames(): List<Game>
}