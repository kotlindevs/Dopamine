package com.google.android.piyush.youtube.utilities

sealed class Response<out T> {

    data class Success<out T>(
        val data: T
    ) : Response<T>()

    data class Error(
        val exception: Exception
    ) : Response<Nothing>()

    data object Loading : Response<Nothing>()

    override fun toString(): String {
        return when(this){
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}