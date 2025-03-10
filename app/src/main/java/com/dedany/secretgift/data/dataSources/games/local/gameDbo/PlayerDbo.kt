package com.dedany.secretgift.data.dataSources.games.local.gameDbo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "players")

data class PlayerDbo(
    val name: String,
    @PrimaryKey val email: String
) : Serializable
