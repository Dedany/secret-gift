package com.dedany.secretgift.data.dataSources.games.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.PlayerDbo

@Dao
interface GamesDao {

    @Query("SELECT * FROM games")
    suspend fun getGames(): List<GameDbo>

    @Query("DELETE FROM games")
    suspend fun deleteAllGames()

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGame(gameId: Int): GameDbo

    @Query("SELECT * FROM games WHERE id = :id LIMIT 1")
    suspend fun getLocalGameById(id: Int): GameDbo

    @Query("SELECT * FROM games WHERE ownerId = :userId")
    suspend fun getLocalGamesByUser(userId: String): List<GameDbo>

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGame(gameId: Int): Int

    @Insert
    suspend fun createLocalGame(game: GameDbo): Long

    @Update
    suspend fun updateLocalGame(game: GameDbo): Int
}