package com.dedany.secretgift.data.dataSources.games.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreatePlayerDto(
    @SerializedName("player_name")
    val name: String,
    val email: String,
    @SerializedName("linked_to")
    val linkedTo: String
): Serializable
