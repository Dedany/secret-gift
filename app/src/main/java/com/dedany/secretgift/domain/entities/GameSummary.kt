package com.dedany.secretgift.domain.entities

import java.io.Serializable
import java.util.Date

data class GameSummary(
    val id: String,
    val name: String,
    val status: String,
    val accessCode: String,
    val date: Date,
    val isOwnedGame: Boolean =false
): Serializable
