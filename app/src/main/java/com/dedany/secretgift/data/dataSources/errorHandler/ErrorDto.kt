package com.dedany.secretgift.data.dataSources.errorHandler

import com.google.gson.annotations.SerializedName

data class ErrorDto(
    @SerializedName("message")
    val errorMessage: String,


): Exception()
