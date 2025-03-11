package com.dedany.secretgift.data.dataSources.games.local.gameDbo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dedany.secretgift.data.Converters

import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "games")
data class GameDbo(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerializedName("game_name")
    val name: String,
    @SerializedName("owner_id")
    val ownerId: String,
    @SerializedName("max_cost")
    val maxCost: Int?,
    @SerializedName("min_cost")
    val minCost: Int?,
    val players: List<PlayerDbo>,
    @SerializedName("game_date")
    val gameDate: Date= Date(),
    val rules: List<RuleDbo> = emptyList()
)