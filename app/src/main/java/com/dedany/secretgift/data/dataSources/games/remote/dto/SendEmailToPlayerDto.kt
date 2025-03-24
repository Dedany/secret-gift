package com.dedany.secretgift.data.dataSources.games.remote.dto

data class SendEmailToPlayerDto(
    val gameId: String,
    val playerId: String,
    val playerEmail: String
)
