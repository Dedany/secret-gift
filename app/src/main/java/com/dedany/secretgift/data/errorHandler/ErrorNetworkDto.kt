package com.dedany.secretgift.data.errorHandler

sealed class NetworkErrorDto() : Exception() {
    object NoInternetConnection : NetworkErrorDto()
    object TimeOutError : NetworkErrorDto()
    data class FailureError(val code: Int, val body: String) : NetworkErrorDto()
    object UnknownErrorDto : NetworkErrorDto()
}
