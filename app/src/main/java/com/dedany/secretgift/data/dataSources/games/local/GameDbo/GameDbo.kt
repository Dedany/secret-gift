package com.dedany.secretgift.data.dataSources.games.local.GameDbo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dedany.secretgift.domain.entities.GuestUser
import com.dedany.secretgift.domain.entities.User

@Entity(tableName = "games")
data class GameDbo(
    @PrimaryKey val id: String,
    val ownerId: String,
    val averageCost: Int,
    val status: String,
    val roomCode: String,
//    val players: ArrayList<GuestUser>,
)