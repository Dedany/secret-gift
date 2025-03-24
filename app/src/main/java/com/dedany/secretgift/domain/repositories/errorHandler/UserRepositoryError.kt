package com.dedany.secretgift.domain.repositories.errorHandler

sealed class UserRepositoryError(message: String) : Exception(message) {
    class UserNotFoundError(message: String) : UserRepositoryError(message)
    class UnexpectedError(message: String) : UserRepositoryError(message)
    class DataConversionError(message: String) : GameRepositoryError(message)
}