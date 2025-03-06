package com.dedany.secretgift.data.dataSources.games.remote.dto

import com.google.gson.annotations.SerializedName

data class GameRuleDto(
    @SerializedName("player_1")
    val playerOne: String,
    @SerializedName("player_2")
    val playerTwo: String
)