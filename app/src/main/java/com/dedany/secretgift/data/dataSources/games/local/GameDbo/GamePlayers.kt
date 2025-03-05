package com.dedany.secretgift.data.dataSources.games.local.GameDbo

import androidx.room.Entity

@Entity(tableName = "game_players", primaryKeys = ["gameId", "playerId"])
data class GamePlayers(
    val gameId: String,
    val playerId: String
)
