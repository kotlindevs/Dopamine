package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerResponse(
    val videoDetails : VideoDetails? = null
){
    @Serializable
    data class VideoDetails(
        val videoId : String? = null,
        val title : String? = null,
        val channelId : String? = null,
        val shortDescription : String? = null,
        val thumbnail : Thumbnail? = null,
        val author : String? = null
    ){
        @Serializable
        data class Thumbnail(
            val thumbnails : List<Thumbnails>? = null
        ){
            @Serializable
            data class Thumbnails(
                val url : String? = null,
                val width : Int? = null,
                val height : Int? = null
            )
        }
    }
}
