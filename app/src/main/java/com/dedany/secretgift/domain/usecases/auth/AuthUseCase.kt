package com.dedany.secretgift.domain.usecases.auth

interface AuthUseCase {

    fun login(email:String ,password: String):Boolean
}