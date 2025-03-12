package com.dedany.secretgift.data.dataSources.games.local.gameDbo


import androidx.room.PrimaryKey
import java.io.Serializable

data class PlayerDbo(
    val name: String,
    @PrimaryKey val email: String
) : Serializable
