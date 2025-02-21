package com.dedany.secretgift.domain.entities

data class RegisteredUser(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
)
