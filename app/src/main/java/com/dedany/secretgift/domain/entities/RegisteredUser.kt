package com.dedany.secretgift.domain.entities

data class RegisteredUser(
    override val id: String,
    override val name: String,
    val email: String,
    val password: String,
): User
