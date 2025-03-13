package com.dedany.secretgift.data.dataSources.games.remote.dto

import com.google.gson.annotations.SerializedName

data class SavePlayerDto(
    @SerializedName("player_name")
    val name: String,
    val email: String,
    @SerializedName("linked_to")
    val linkedTo: String? = null
)
