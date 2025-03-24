package com.dedany.secretgift.data.dataSources.users.remote

import com.dedany.secretgift.data.errorHandler.NetworkErrorDto
import com.dedany.secretgift.data.dataSources.games.remote.dto.UserRegisteredDto
import com.dedany.secretgift.data.dataSources.users.api.UsersApi
import com.dedany.secretgift.data.dataSources.users.remote.dto.CreateUserDto
import com.dedany.secretgift.data.dataSources.users.remote.dto.UserEmailDto
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class UsersRemoteDataSourceImpl @Inject constructor(
    private val usersApi: UsersApi
) : UsersRemoteDataSource {

    override suspend fun signUpUser(user: CreateUserDto): CreateUserDto {
        return try {
            usersApi.signUpUser(user)
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }

    override suspend fun getUserByEmail(email: UserEmailDto): Response<UserRegisteredDto> {
        return try {
            usersApi.getUserByEmail(email)
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }

    private fun handleNetworkException(e: Exception): NetworkErrorDto {
        return when (e) {
            is HttpException -> {
                val code = e.response()?.code() ?: -1
                val body = e.response()?.errorBody()?.string() ?: "Unknown HTTP error"
                NetworkErrorDto.FailureError(code, body)
            }
            is IOException -> {
                NetworkErrorDto.NoInternetConnection
            }
            else -> {
                NetworkErrorDto.UnknownErrorDto
            }
        }
    }
}

