package com.dedany.secretgift.data.dataSources.users.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("player_code") val playerCode: String? = null,
    @SerializedName("linked_to") val linkedTo: String? = null,
    @SerializedName("owned_games") val ownedGames: String? = null,
    @SerializedName("played_games") val playedGames: String? = null,
)
