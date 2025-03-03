package com.dedany.secretgift.data.dataSources.games.remote.dto

import com.dedany.secretgift.domain.entities.User
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.util.Date

data class GameDto(
    @SerializedName("_id")
    var id: String,
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

    @SerializedName("players")
    val players: List<PlayerDto>,

    @SerializedName("game_date")
    val gameDate: Date,

    @SerializedName("rules")
    val rules: List<GameRuleDto>

)
