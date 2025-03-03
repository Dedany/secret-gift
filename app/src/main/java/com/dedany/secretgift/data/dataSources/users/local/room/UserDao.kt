package com.dedany.secretgift.data.dataSources.users.local.room

import androidx.room.Query

interface UserDao {

    @Query ("SELECT * FROM users")
    fun getAllUsers(): List<UserDbo>
}