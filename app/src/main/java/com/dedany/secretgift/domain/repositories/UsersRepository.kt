package com.dedany.secretgift.domain.repositories

import com.dedany.secretgift.domain.entities.User

interface UsersRepository {

    suspend fun getRegisteredUser(): User
}