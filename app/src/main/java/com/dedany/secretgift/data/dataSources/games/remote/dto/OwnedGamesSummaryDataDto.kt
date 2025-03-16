package com.dedany.secretgift.data.dataSources.games.remote.dto

import com.google.gson.annotations.SerializedName

data class OwnedGamesSummaryDataDto(
    @SerializedName("owned_games")
    val data: List<GameSummaryDto>

)
