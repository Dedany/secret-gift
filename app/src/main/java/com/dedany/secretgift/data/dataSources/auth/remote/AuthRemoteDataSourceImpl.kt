package com.dedany.secretgift.data.dataSources.auth.remote

import com.dedany.secretgift.data.dataSources.auth.remote.dto.LoginDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.UserRegisteredDto
import com.dedany.secretgift.data.dataSources.users.remote.UsersRemoteDataSource
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserEmailDto
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRemoteDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val usersRemoteDataSource: UsersRemoteDataSource,
) : AuthRemoteDataSource {
    override suspend fun login(loginDto: LoginDto): LoginDto {
        return suspendCoroutine { result ->
            try {
                auth.signInWithEmailAndPassword(loginDto.email, loginDto.password)
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
        auth.signOut()
        return true
    }

    override suspend fun register(createUserDto: CreateUserDto): Pair<Boolean, String> {
        val uuid = createAuthUser(createUserDto.email, createUserDto.password)

        var isRegisterSuccess = false
        if (uuid.isNotEmpty()) {
            isRegisterSuccess = true
            val userDtoWithoutPassword = createUserDto.copy(password = "")
            usersRemoteDataSource.signUpUser(userDtoWithoutPassword)

        }
        return Pair(isRegisterSuccess, uuid)
    }

    override suspend fun getUserByEmail(email: UserEmailDto): Response<UserRegisteredDto> {
        return usersRemoteDataSource.getUserByEmail(email)
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