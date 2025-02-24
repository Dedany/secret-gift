package com.dedany.secretgift.data.dataSources.games.remote.dto

import com.google.gson.annotations.SerializedName

data class PlayerDto(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val email: String,
    @SerializedName("player_code")
    val playerCode: String,
    @SerializedName("linked_to")
    val linkedTo: String,


    )
