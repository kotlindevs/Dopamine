package com.google.android.piyush.youtube.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class GridChannelRenderer (
    val channelId : String? = null,
    val thumbnail : Thumbnails? = null,
    val videoCountText : VideoCountText? = null,
    val subscriberCountText : SubscriberCountText? = null,
    val title : Title? = null,
){
    @Serializable
    data class Thumbnails(
        val thumbnails : List<Thumbnail>? = null
    ){
        @Serializable
        data class Thumbnail(
            val url : String? = null,
            val width : Int? = null,
            val height : Int? = null
        )
    }

    @Serializable
    data class VideoCountText(
        val runs : List<Run>? = null
    ){
        @Serializable
        data class Run(
            val text : String? = null
        )
    }

    @Serializable
    data class SubscriberCountText(
        val simpleText : String? = null
    )

    @Serializable
    data class Title(
        val simpleText : String? = null
    )
}