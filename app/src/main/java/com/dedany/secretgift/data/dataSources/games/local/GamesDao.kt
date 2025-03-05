package com.dedany.secretgift.data.dataSources.games.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dedany.secretgift.data.dataSources.games.local.GameDbo.GameDbo
import com.dedany.secretgift.domain.entities.Game

@Dao
interface GamesDao {

    @Query("SELECT * FROM games")
    fun getGames(): List<GameDbo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllGames(gamesdbo: List<GameDbo>)

    @Query("""
        SELECT * FROM games 
        WHERE id IN (
            SELECT gameId FROM game_players WHERE playerId = :userId
        )
    """)
    suspend fun getGamesByUser(userId: String): List<GameDbo>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameDbo)

    @Delete
    fun delete(game: GameDbo)
}