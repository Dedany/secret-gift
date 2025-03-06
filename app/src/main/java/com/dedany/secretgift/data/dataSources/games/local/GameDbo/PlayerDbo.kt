package com.dedany.secretgift.data.dataSources.games.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "players")
data class PlayerDbo(
    @SerializedName("_id")
    @PrimaryKey
    val id: String,
    @SerializedName("player_name")
    val name: String,
    val email: String,
)