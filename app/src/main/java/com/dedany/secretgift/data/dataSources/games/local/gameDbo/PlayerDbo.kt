package com.dedany.secretgift.data.dataSources.games.local.gameDbo

import java.io.Serializable


data class PlayerDbo(
    val name: String,
    val email: String
) : Serializable
