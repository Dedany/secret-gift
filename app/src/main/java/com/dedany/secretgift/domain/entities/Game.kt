package com.dedany.secretgift.domain.entities

import java.time.LocalDate
import java.util.Date


data class Game(
    val id: String? = null,
    val ownerId: String,
    val status: String,
    val gameCode: String,
    val players: List<User> = emptyList(),
    val name: String,
    val maxCost : Int,
    val minCost : Int,
    val gameDate : Date
)


