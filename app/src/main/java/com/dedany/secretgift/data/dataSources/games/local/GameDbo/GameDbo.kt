package com.dedany.secretgift.data.dataSources.games.local.GameDbo

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dedany.secretgift.data.Converters
import com.dedany.secretgift.data.dataSources.games.local.PlayerDbo
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "games")
@TypeConverters(Converters::class)
data class GameDbo(
    @PrimaryKey val id: String,
    val name: String,
    @SerializedName("owner_id")
    val ownerId: String,
    @SerializedName("max_cost")
    val maxCost: Int?,
    @SerializedName("min_cost")
    val minCost: Int?,
    val status: String,
    @SerializedName("game_code")
    val gameCode: String,
    val players: List<PlayerDbo>,
    @SerializedName("game_date")
    val gameDate: Date
)