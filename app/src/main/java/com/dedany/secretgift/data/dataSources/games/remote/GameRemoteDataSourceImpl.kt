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
/*
     suspend fun getGames2(): List<GameDto> {
        return suspendCoroutine {
            firestore.collection("games").get()
                .addOnSuccessListener { result ->
                    val games: List<GameDto> = result.toObjects(GameDto::class.java)
                    games.forEachIndexed{ index, game ->
                        game.id = result.documents.getOrNull(index)?.id.toString()
                    }
                    it.resume(games)
                }.addOnFailureListener{ _ ->
                    it.resume(emptyList())}
        }
    }
*/
    override suspend fun getGames(): List<GameDto> {
        val games = gamesApi.getGames().body()?.data ?: emptyList()
        return games
    }

}
