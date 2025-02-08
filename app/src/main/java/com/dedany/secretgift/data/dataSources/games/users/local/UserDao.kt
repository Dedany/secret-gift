package com.dedany.secretgift.data.dataSources.games.users.local

import androidx.room.Query
import com.dedany.secretgift.data.dataSources.games.users.local.UserDbo.UserDbo

interface UserDao {

    @Query ("SELECT * FROM users")
    fun getAllUsers(): List<UserDbo>
}