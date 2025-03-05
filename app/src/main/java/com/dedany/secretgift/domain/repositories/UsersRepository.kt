package com.dedany.secretgift.domain.repositories

import com.dedany.secretgift.domain.entities.RegisteredUser

interface UsersRepository {

    suspend fun getRegisteredUser(): RegisteredUser
}