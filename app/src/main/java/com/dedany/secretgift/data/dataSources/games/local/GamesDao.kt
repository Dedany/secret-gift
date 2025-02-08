package com.dedany.secretgift.data.dataSources.games.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dedany.secretgift.data.dataSources.games.local.GameDbo.GameDbo

@Dao
interface GamesDao {

    @Query("SELECT * FROM games")
    fun getGames(): List<GameDbo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllGames(gamesdbo: List<GameDbo>)
}