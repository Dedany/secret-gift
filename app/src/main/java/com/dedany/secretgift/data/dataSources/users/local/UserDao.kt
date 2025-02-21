package com.dedany.secretgift.data.dataSources.users.local

import androidx.room.Query
import com.dedany.secretgift.data.dataSources.users.local.dbo.UserDbo

interface UserDao {

    @Query ("SELECT * FROM users")
    fun getAllUsers(): List<UserDbo>
}