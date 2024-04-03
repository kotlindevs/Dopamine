package com.google.android.piyush.dopamine.viewModels

sealed class RealtimeResource <out T>(
    val data : T? = null,
    val message : String? = null
) {
    class Success<T>(data: T) : RealtimeResource<T>(data)
    class Error<T>(message: String, data: T? = null) : RealtimeResource<T>(data, message)
    class Loading<T> : RealtimeResource<T>()
}