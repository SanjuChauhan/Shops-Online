package com.app.lovelocal_assignment.api

import retrofit2.HttpException
import java.net.SocketTimeoutException

enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1)
}

open class ResponseHandler {
    fun <T : Any> handleSuccess(data: T): Result<T> {
        return Result.success(data)
    }

    fun <T : Any> handleException(e: Exception): Result<T> {
        return when (e) {
            is HttpException -> Result.error(getErrorMessage(e.code()), null)
            is SocketTimeoutException -> Result.error(
                getErrorMessage(ErrorCodes.SocketTimeOut.code),
                null
            )
            else -> Result.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            ErrorCodes.SocketTimeOut.code -> "Timeout"
            401 -> "Unauthorised"
            404 -> "Not found"
            else -> "Something went wrong"
        }
    }
}
