package com.dedany.secretgift.domain.entities

import java.io.Serializable
import java.util.Date

data class Game(
    val id: String,
    val ownerId: String,
    val status: String,
    val gameCode: String,
    val players: List<Player> = emptyList(),
    var name: String,
    val maxCost : Int?,
    val minCost : Int?,
    val gameDate : Date,
    val currentPlayer : String,
    val matchedPlayer : String
): Serializable


