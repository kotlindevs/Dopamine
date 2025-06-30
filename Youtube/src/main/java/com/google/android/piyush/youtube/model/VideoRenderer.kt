package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoRenderer(
    val videoId : String? = null,
    val thumbnail : Thumbnail? = null,
    val title : Title? = null,
    val longBylineText : LongBylineText? = null,
    val lengthText : LengthText? = null,
    val publishedTimeText : PublishedTimeText? = null,
    val shortViewCountText : ShortViewCountText? = null,
    val avatar : Avatar? = null,
    val channelThumbnailSupportedRenderers : ChannelThumbnailSupportedRenderers? = null
){
    @Serializable
    data class Thumbnail(
        val thumbnails : List<Thumbnails>? = null
    ){
        @Serializable
        data class Thumbnails(
            val url : String? = null,
            val width : Int? = 0,
            val height : Int? = 0
        )
    }
    @Serializable
    data class Title(
        val runs : List<Runs>? = null
    ){
        @Serializable
        data class Runs(
            val text : String? = null
        )
    }
    @Serializable
    data class LongBylineText(
        val runs : List<Runs>? = null
    ){
        @Serializable
        data class Runs(
            val text : String? = null,
        )
    }
    @Serializable
    data class PublishedTimeText(
        val simpleText : String? = null
    )
    @Serializable
    data class ShortViewCountText(
        val simpleText : String? = null
    )

    @Serializable
    data class LengthText(
        val simpleText : String? = null
    )
    @Serializable
    data class ChannelThumbnailSupportedRenderers(
        val channelThumbnailWithLinkRenderer : ChannelThumbnailWithLinkRenderer? = null
    ){
        @Serializable
        data class ChannelThumbnailWithLinkRenderer(
            val thumbnail : Thumbnail? = null
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
}