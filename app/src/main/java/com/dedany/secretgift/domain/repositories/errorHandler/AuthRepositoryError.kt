package com.dedany.secretgift.domain.repositories.errorHandler

sealed class AuthRepositoryError(message: String) : Exception(message) {
    class InvalidCredentialsError(message: String) : AuthRepositoryError(message)
    class UserNotFoundError(message: String) : AuthRepositoryError(message)
    class UnexpectedError(message: String) : AuthRepositoryError(message)
    class UserRegistrationError(message: String) : AuthRepositoryError(message)
}