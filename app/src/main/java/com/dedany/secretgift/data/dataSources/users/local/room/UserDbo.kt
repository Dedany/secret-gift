package com.dedany.secretgift.data.dataSources.users.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class UserDbo(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val password: String? = null,
    val playerCode: String? = null,
    val linkedTo: String? = null,
    val gameCodes: List<String> = emptyList()
)
