package com.dedany.secretgift.data.dataSources.games.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dedany.secretgift.data.dataSources.games.local.GameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.GameDbo.GamePlayerDbo
import com.dedany.secretgift.domain.entities.Game

@Dao
interface GamesDao {

    @Query("SELECT * FROM games")
    fun getGames(): List<GameDbo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllGames(gamesdbo: List<GameDbo>)

    @Query("""
        SELECT games.* FROM games
        INNER JOIN game_players ON games.id = game_players.gameId
        WHERE game_players.playerId = :userId
    """)
    suspend fun getGamesByUser(userId: String): List<GameDbo>

    @Insert
    suspend fun insertGamePlayers(gamePlayers: List<GamePlayerDbo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameDbo)

    @Delete
    fun delete(game: GameDbo)
}