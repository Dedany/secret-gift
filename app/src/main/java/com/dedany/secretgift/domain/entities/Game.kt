package com.dedany.secretgift.domain.entities

import java.time.LocalDate


data class Game(
    val id: String,
    val ownerId: String,
    val status: String,
    val gameCode: String,
    val players: List<GuestUser> = emptyList(),
    val name: String,
    val maxCost : Int,
    val minCost : Int,
    val gameDate : LocalDate
)


