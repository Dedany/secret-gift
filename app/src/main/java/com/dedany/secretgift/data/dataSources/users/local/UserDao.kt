package com.dedany.secretgift.data.dataSources.users.local

import androidx.room.Dao
import androidx.room.Query
import com.dedany.secretgift.data.dataSources.games.users.local.dbo.UserDbo


@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<UserDbo>
}