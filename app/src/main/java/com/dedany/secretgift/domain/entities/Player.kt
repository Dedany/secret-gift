package com.dedany.secretgift.domain.entities

import java.io.Serializable

data class Player(
    var name: String,
    val email: String
): Serializable
