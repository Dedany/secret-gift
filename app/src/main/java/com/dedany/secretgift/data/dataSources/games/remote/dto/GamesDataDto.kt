package com.dedany.secretgift.data.dataSources.games.remote.dto

import com.google.gson.annotations.SerializedName

data class GamesDataDto(
    @SerializedName("games")
    val data: List<GameDto>
)
