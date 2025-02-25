package com.dedany.secretgift.domain.usecases.auth

import com.dedany.secretgift.data.dataSources.users.remote.dto.UserDto

interface AuthUseCase {

    suspend fun login(email:String, password: String):Boolean
    fun isEmailFormatValid(email: String): Boolean
    fun isPasswordFormatValid(password: String): Boolean
    fun isLoginFormValid(email: String, password: String): Boolean
    fun isNameValid(name: String): Boolean
    fun isPasswordMatching(value: String, repeatPassword: String): Boolean
    fun isRegisterFormValid(name: String, age: Int, email: String, password: String, repeatPassword: String): Boolean
    suspend fun getUsers(): List<UserDto>
}