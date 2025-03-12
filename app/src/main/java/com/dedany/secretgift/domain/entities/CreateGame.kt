package com.dedany.secretgift.domain.entities

import java.util.Date

data class CreateGame(
    val name: String,
    val ownerId: String,
    val status: String,
    val players: List<CreatePlayer>,
    val maxCost: Int?,
    val minCost: Int?,
    val gameDate: Date,
    val rules: List<Rule> = emptyList()
)
