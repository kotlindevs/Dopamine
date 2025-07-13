package com.google.android.piyush.youtube.utilities

sealed class YoutubeResource<out T> {

    data class Success<out T>(
        val data: T
    ) : YoutubeResource<T>()

    data class Error(
        val exception: Exception
    ) : YoutubeResource<Nothing>()

    data object Loading : YoutubeResource<Nothing>()

    override fun toString(): String {
        return when(this){
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}