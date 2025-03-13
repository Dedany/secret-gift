package com.dedany.secretgift.domain.entities

import java.io.Serializable

data class SavePlayer(
    val name: String,
    val email: String,
    val linkedTo: String
): Serializable
