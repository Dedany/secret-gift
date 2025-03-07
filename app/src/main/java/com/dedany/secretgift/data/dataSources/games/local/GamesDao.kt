package com.dedany.secretgift.data.dataSources.games.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.GameDbo

@Dao
interface GamesDao {

    @Query("SELECT * FROM games")
    suspend fun getGames(): List<GameDbo>

    @Delete
    suspend fun deleteLocalGame(game: GameDbo)

    @Insert
    suspend fun createLocalGame(game: GameDbo)

    @Update
    suspend fun updateLocalGame(game: GameDbo)
}