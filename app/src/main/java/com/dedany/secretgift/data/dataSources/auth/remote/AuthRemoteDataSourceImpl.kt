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
import kotlin.coroutines.resumeWithException
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
                        if (authResult.isSuccessful) {
                            val user = authResult.result?.user
                            val userId = user?.uid ?: ""
                            result.resume(LoginDto(userId, loginDto.email, userId))
                        } else {
                            val exception = authResult.exception
                            println("Login failed: ${exception?.message}")
                            result.resumeWithException(
                                Exception("Error de inicio de sesión: ${exception?.message}")
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        println("Login failed with exception: ${exception.stackTraceToString()}")
                        result.resumeWithException(Exception("Error en el proceso de autenticación"))
                    }
            } catch (e: Exception) {
                println("Unexpected error: ${e.stackTraceToString()}")
                result.resumeWithException(Exception("Error inesperado en el inicio de sesión"))
            }
        }
    }

    override fun logout(): Boolean {
        auth.signOut()
        return true
    }

    override fun isLoggedIn(): Boolean {
        return auth.currentUser != null
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