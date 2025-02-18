package com.dedany.secretgift.domain.entities


data class Game(
    val id: String,
    val ownerId: String,
    val averageCost: Int,
    val status: String,
    val roomCode: String,
    val players: List<GuestUser> = emptyList(),
    val name: String
)


