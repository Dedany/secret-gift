package com.dedany.secretgift.data.dataSources.games.remote

import com.dedany.secretgift.data.dataSources.games.remote.api.SecretGiftApi
import com.dedany.secretgift.data.dataSources.games.remote.dto.GameDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GameRemoteDataSourceImpl @Inject constructor(
    private val gamesApi: SecretGiftApi,
   // private val firestore: FirebaseFirestore
):GameRemoteDataSource {
    override suspend fun getGame(gameCode: String): GameDto {
       val game = gamesApi.getGameByAccessCode(gameCode).body()
        return game!!
    }

    override suspend fun getGames(): List<GameDto> {
        val games = gamesApi.getGames().body()?.data ?: emptyList()
        return games
    }

}
