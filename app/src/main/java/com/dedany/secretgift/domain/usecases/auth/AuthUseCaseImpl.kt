package com.dedany.secretgift.domain.usecases.auth

import com.dedany.secretgift.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthUseCaseImpl @Inject constructor(private val repository: AuthRepository): AuthUseCase  {
    override fun login(email: String, password: String): Boolean {
        TODO("Not yet implemented")
    }
}