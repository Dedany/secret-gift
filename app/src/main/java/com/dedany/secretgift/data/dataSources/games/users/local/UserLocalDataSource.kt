package com.dedany.secretgift.data.dataSources.games.users.local

import com.dedany.secretgift.data.dataSources.games.users.local.UserDbo.UserDbo

interface UserLocalDataSource {
    suspend fun getUsers(): List<UserDbo>
}