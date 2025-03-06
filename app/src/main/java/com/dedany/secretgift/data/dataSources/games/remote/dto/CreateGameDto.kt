package com.dedany.secretgift.data.dataSources.games.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CreateGameDto(
    @SerializedName("game_name")
    val name: String,

    @SerializedName("owner_id")
    val ownerId: String,

    @SerializedName("max_cost")
    val maxCost: Int,
    @SerializedName("min_cost")
    val minCost: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("game_code")
    val gameCode: String,

    val players: List<UserRegisteredDto>,

    @SerializedName("game_date")
    val gameDate: Date,

    val rules: List<GameRuleDto>
)
