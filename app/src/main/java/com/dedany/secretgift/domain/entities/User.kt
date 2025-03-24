package com.dedany.secretgift.domain.entities


import java.io.Serializable

data class User(
    val id: String,
    val name: String,
    val email: String,
    val mailStatus: String?=null
) : Serializable
