package com.dedany.secretgift.data.dataSources.users.local.room

interface UserLocalDataSource {
    suspend fun getUsers(): List<UserDbo>
}