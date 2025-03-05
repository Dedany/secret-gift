package com.dedany.secretgift.domain.entities

import java.io.Serializable

data class Player(
    val id: String,
    val name: String,
    val email: String,
    val playerCode: String,
): Serializable
