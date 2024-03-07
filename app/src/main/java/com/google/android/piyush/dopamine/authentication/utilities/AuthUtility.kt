package com.google.android.piyush.dopamine.authentication.utilities

import com.google.android.piyush.dopamine.authentication.Status

sealed class GoogleAuth<out T>(
    val data : T? = null,
    val message : String? = null
) {
    class Success<T>(data : T) : GoogleAuth<T>(data)
    class Error<T>(message: String?,data: T? = null) : GoogleAuth<T>(data,message)
    class Loading<T>(data: T? = null) : GoogleAuth<T>(data)
}

data class PhoneNumberAuth<out T>(
    val status : Status,
    val data : T?,
    val message : String?
) {
    companion object {

        fun <T> success(data : T?) : PhoneNumberAuth<T> {
            return PhoneNumberAuth(Status.SUCCESS, data, null)
        }

        fun <T> error(msg : String, data : T?) : PhoneNumberAuth<T> {
            return PhoneNumberAuth(Status.ERROR, data, msg)
        }

        fun <T> loading(data : T?) : PhoneNumberAuth<T> {
            return PhoneNumberAuth(Status.LOADING, data, null)
        }

    }
}
