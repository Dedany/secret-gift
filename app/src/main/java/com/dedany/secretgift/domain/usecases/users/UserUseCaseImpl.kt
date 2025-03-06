package com.dedany.secretgift.domain.usecases.users

import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.domain.repositories.UsersRepository
import javax.inject.Inject

class UserUseCaseImpl @Inject constructor(
    private val repository: UsersRepository
) : UsersUseCase {

    override suspend fun getRegisteredUser(): User {
        return repository.getRegisteredUser()
    }
}
