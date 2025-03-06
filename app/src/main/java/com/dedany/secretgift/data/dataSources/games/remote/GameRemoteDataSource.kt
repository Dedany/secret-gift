package com.dedany.secretgift.data.dataSources.games.remote

import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto

interface GameRemoteDataSource {
    suspend fun getGamesByUser(): List<GameDto>
}