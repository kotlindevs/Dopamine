package com.google.android.piyush.dopamine.authentication.utilities

sealed class GoogleAuth<out T>(
    val data : T? = null,
    val message : String? = null
) {
    class Success<T>(data : T) : GoogleAuth<T>(data)
    class Error<T>(message: String?,data: T? = null) : GoogleAuth<T>(data,message)
    class Loading<T>(data: T? = null) : GoogleAuth<T>(data)
}
