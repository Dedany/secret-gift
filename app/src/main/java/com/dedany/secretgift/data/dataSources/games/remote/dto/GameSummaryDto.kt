package com.dedany.secretgift.data.dataSources.games.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

data class GameSummaryDto(

    var id: String,

    val name: String,
    @SerializedName("game_status")
    val status: String,
    @SerializedName("access_code")
    val accessCode: String,
    @SerializedName("game_date")
    val gameDate: Date,
): Serializable
