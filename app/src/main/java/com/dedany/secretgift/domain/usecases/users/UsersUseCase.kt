package com.dedany.secretgift.domain.usecases.users

import com.dedany.secretgift.domain.entities.RegisteredUser

interface UsersUseCase {
    suspend fun getRegisteredUser(): RegisteredUser
}