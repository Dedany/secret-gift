package com.dedany.secretgift.domain.entities

import java.io.Serializable
import java.util.Date

data class LocalGame(
    val id: Int = 0,
    val ownerId: String,
    val players: List<Player> = emptyList(),
    var name: String,
    val maxCost: Int?=null,
    val minCost: Int?=null,
    val gameDate: Date? = Date(),
    val rules: List<Rule> = emptyList()
):Serializable
