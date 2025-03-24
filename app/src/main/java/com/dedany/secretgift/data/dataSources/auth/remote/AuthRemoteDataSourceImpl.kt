package com.dedany.secretgift.data.dataSources.auth.remote

import com.dedany.secretgift.data.dataSources.auth.remote.dto.LoginDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.UserRegisteredDto
import com.dedany.secretgift.data.dataSources.users.remote.UsersRemoteDataSource
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserEmailDto
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
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
        return suspendCancellableCoroutine { continuation ->
            auth.signInWithEmailAndPassword(loginDto.email, loginDto.password)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        val userId = authResult.result?.user?.uid ?: ""
                        if (continuation.isActive) {
                            continuation.resume(LoginDto(userId, loginDto.email, userId))
                        }
                    } else {
                        if (continuation.isActive) {
                            continuation.resumeWithException(Exception("Error de inicio de sesión"))
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(Exception("Error en el proceso de autenticación"))
                    }
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

    override suspend fun sendResetPasswordEmail(email: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (continuation.isActive) {
                        continuation.resume(task.isSuccessful)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(false)
                    }
                }
        }
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