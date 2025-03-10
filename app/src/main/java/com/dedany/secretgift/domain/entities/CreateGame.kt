package com.dedany.secretgift.domain.entities

data class CreateGame(
    val name: String,
    val ownerId: String,
    val players: List<CreatePlayer>,
    val maxCost: Int?,
    val minCost: Int?,
    val gameDate: String,
    val rules: List<Rule> = emptyList()
)
