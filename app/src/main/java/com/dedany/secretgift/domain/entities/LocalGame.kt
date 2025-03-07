package com.dedany.secretgift.domain.entities

import java.util.Date

data class LocalGame(
    val id: Int = 0,
    val ownerId: String,
    val players: List<Player> = emptyList(),
    var name: String,
    val maxCost: Int?=null,
    val minCost: Int?=null,
    val gameDate: Date? = null,
    val rules: List<Rule> = emptyList()
)
