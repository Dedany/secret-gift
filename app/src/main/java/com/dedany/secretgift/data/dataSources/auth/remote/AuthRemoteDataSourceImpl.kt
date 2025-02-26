package com.dedany.secretgift.data.dataSources.auth.remote

import com.dedany.secretgift.data.dataSources.auth.remote.dto.LoginDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserDto
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRemoteDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRemoteDataSource {
    override suspend fun login(loginDto: LoginDto): LoginDto {
        return suspendCoroutine { result ->
            try {
                auth.signInWithEmailAndPassword(loginDto.user, loginDto.password)
                    .addOnCompleteListener { authResult ->
                        try {
                            result.resume(LoginDto("", "", authResult.result?.user?.uid))
                        } catch (e: Exception) {
                            println(e.stackTraceToString())
                            throw e
                        }
                    }
                    .addOnFailureListener { exception ->
                        println(exception.stackTraceToString())
                    }
            } catch (e: Exception) {
                println(e.stackTraceToString())
            }
        }
    }
    override fun logout(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun register(userDto: UserDto): Pair<Boolean, String> {
        val uuid = createAuthUser(userDto.email, userDto.password)
        var isRegisterSuccess = false
        if (uuid.isNotEmpty()) {
            isRegisterSuccess = true
        }
        return Pair(isRegisterSuccess, uuid)
    }

    override suspend fun getUsers(): List<UserDto> {
        TODO("Not yet implemented")
    }
    private suspend fun createAuthUser(email: String, password: String): String {
        return suspendCoroutine { result ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        result.resume(task.result.user?.uid ?: "")
                    }
                }.addOnFailureListener {
                    result.resume("")
                }
        }

    }
}