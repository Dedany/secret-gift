package com.dedany.secretgift.domain.usecases.users

import com.dedany.secretgift.domain.entities.User

interface UsersUseCase {
    suspend fun getRegisteredUser(): User
}