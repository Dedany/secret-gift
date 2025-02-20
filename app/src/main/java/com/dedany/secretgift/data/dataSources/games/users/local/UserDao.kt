package com.dedany.secretgift.data.dataSources.games.users.local

import androidx.room.Query
import com.dedany.secretgift.data.dataSources.games.users.local.dbo.UserDbo

interface UserDao {

    @Query ("SELECT * FROM users")
    fun getAllUsers(): List<UserDbo>
}