package com.dedany.secretgift.data.dataSources.games.local.GameDbo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.util.Date

@Entity(tableName = "games")
data class GameDbo(
    @PrimaryKey val id: String,
    val name: String,
    @SerializedName("owner_id")
    val ownerId: String,
    @SerializedName("max_cost")
    val maxCost: Int,
    @SerializedName("min_cost")
    val minCost: Int,
    val status: String,
    @SerializedName("game_code")
    val gameCode: String,
    //@TypeConverters(Converters::class)val players: ArrayList<UserDbo>
    @SerializedName("game_date")
    val gameDate: Date
)