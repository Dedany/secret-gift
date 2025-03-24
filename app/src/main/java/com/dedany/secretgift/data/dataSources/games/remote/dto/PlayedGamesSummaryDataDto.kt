package com.dedany.secretgift.data.dataSources.games.remote.dto

import com.google.gson.annotations.SerializedName

data class PlayedGamesSummaryDataDto(
    @SerializedName("played_games")
    val data: List<GameSummaryDto>
)
