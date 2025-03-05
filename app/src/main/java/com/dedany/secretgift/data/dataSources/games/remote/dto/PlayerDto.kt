package com.dedany.secretgift.data.dataSources.games.remote.dto

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "players")
data class PlayerDto (
    @SerializedName("_id")
    val id: String,
    val name: String,
    val email: String,
)