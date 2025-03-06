package com.dedany.secretgift.data.dataSources.games.remote.dto

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "players")
data class UserRegisteredDto(
    @SerializedName("user_id") val userId: String,
    @SerializedName("user_name") val name: String,
    @SerializedName("user_email") val email: String
)