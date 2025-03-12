package com.dedany.secretgift.domain.entities

import java.io.Serializable

data class CreatePlayer(
    val name: String,
    val email: String,
    val linkedTo: String
): Serializable
