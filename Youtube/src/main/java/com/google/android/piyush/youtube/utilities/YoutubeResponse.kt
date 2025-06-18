package com.google.android.piyush.youtube.utilities

sealed class YoutubeResponse<out T> {

    data class Success<out T>(
        val data: T
    ) : YoutubeResponse<T>()

    data class Error(
        val exception: Exception
    ) : YoutubeResponse<Nothing>()

    data object Loading : YoutubeResponse<Nothing>()

    override fun toString(): String {
        return when(this){
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}