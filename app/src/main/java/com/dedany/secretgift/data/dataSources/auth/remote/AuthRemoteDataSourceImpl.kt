package com.dedany.secretgift.data.dataSources.auth.remote

import com.dedany.secretgift.data.dataSources.auth.remote.dto.LoginDto
import com.dedany.secretgift.data.errorHandler.NetworkErrorDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.UserRegisteredDto
import com.dedany.secretgift.data.dataSources.users.remote.UsersRemoteDataSource
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserEmailDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
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
                            continuation.resumeWithException(NetworkErrorDto.UnknownErrorDto)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(handleAuthException(exception))
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


    override suspend fun register(userDto: CreateUserDto): Pair<Boolean, String> {
        return try {
            val uuid = createAuthUser(userDto.email, userDto.password)
            if (uuid.isNotEmpty()) {
                val userDtoWithoutPassword = userDto.copy(password = "")
                usersRemoteDataSource.signUpUser(userDtoWithoutPassword)
                Pair(true, uuid)
            } else {
                Pair(false, "")
            }
        } catch (e: Exception) {
            throw handleAuthException(e)
        }
    }

    override suspend fun getUserByEmail(email: UserEmailDto): Response<UserRegisteredDto> {
        return try {
            usersRemoteDataSource.getUserByEmail(email)
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }


    private suspend fun createAuthUser(email: String, password: String): String {
        return suspendCoroutine { result ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        result.resume(task.result.user?.uid ?: "")
                    }
                }.addOnFailureListener { exception ->
                    result.resumeWithException(handleAuthException(exception))
                }
        }
    }

    private fun handleNetworkException(e: Exception): NetworkErrorDto {
        return when (e) {
            is HttpException -> {
                val code = e.response()?.code() ?: -1
                val body = e.response()?.errorBody()?.string() ?: ""
                NetworkErrorDto.FailureError(code, body)
            }

            is SocketTimeoutException -> NetworkErrorDto.TimeOutError
            is IOException -> NetworkErrorDto.NoInternetConnection
            else -> NetworkErrorDto.UnknownErrorDto
        }
    }

    private fun handleAuthException(e: Exception): NetworkErrorDto {
        return when (e) {
            is FirebaseAuthException -> NetworkErrorDto.FailureError(
                -1,
                e.message ?: "Error de autenticación"
            )

            is SocketTimeoutException -> NetworkErrorDto.TimeOutError
            is IOException -> NetworkErrorDto.NoInternetConnection
            else -> NetworkErrorDto.UnknownErrorDto
        }

    }
}