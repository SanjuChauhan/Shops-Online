package com.app.lovelocal_assignment.api

interface APIResponseListener {
    fun onSuccess(response: Any?)
    fun onFail(message: String?)
}