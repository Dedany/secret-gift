package com.dedany.secretgift.domain.entities

import java.util.Date

data class CreateGame(
    val ownerId: String,
    val status: String,
    val gameCode: String,
    val players: List<Player> = emptyList(),
    var name: String,
    val maxCost: Int,
    val minCost: Int,
    val gameDate: Date,
    val rules: List<Rule> = emptyList()
)
