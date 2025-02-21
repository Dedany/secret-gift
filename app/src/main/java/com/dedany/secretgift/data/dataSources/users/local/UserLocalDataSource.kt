package com.dedany.secretgift.data.dataSources.users.local

import com.dedany.secretgift.data.dataSources.users.local.dbo.UserDbo

interface UserLocalDataSource {
    suspend fun getUsers(): List<UserDbo>
}