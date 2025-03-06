package com.dedany.secretgift.data.dataSources.games.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.dedany.secretgift.data.dataSources.games.local.GameDbo.GameDbo

@Dao
interface GamesDao {

    @Query("SELECT * FROM games")
    fun getGames(): List<GameDbo>

    @Delete
    fun delete(game: GameDbo)
}