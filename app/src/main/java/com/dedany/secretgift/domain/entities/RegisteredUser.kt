package com.dedany.secretgift.domain.entities


import java.io.Serializable

data class RegisteredUser(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
): Serializable
