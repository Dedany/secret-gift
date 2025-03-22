package com.dedany.secretgift.data.dataSources.games.remote

import com.dedany.secretgift.data.dataSources.games.remote.dto.CreateGameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameSummaryDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.SendEmailToPlayerDto

interface GameRemoteDataSource {
    suspend fun getGame(gameCode: String): GameDto
    suspend fun getGamesByUser(): List<GameSummaryDto>
    suspend fun createGame(game: CreateGameDto):Boolean
    suspend fun deleteGame(gameId: String): Boolean
    suspend fun updateGame(game: GameDto)
    suspend fun sendMailToPlayer(sendEmailToPlayerDto: SendEmailToPlayerDto): Boolean


}