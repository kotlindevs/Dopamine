package com.google.android.piyush.youtube.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
@InternalSerializationApi
@Serializable
data class GridVideoRenderer(
    val videoId : String? = null,
    val thumbnail : Thumbnail? = null,
    val title : Title? = null,
    val publishedTimeText : PublishedTimeText? = null,
    val shortBylineText : ShortBylineText? = null,
    val shortViewCountText : ShortViewCountText? = null,
    val thumbnailOverlays : List<ThumbnailOverlay>? = null
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

    @Serializable
    data class Title(
        val simpleText : String? = null
    )

    @Serializable
    data class PublishedTimeText(
        val simpleText: String? = null
    )

    @Serializable
    data class ShortBylineText(
        val runs : List<Run>? = null
    ){
        @Serializable
        data class Run(
            val text : String? = null,
            val navigationEndpoint : NavigationEndpoint? = null
        ){
            @Serializable
            data class NavigationEndpoint(
                val browseEndpoint : BrowseEndpoint? = null
            ){
                @Serializable
                data class BrowseEndpoint(
                    val browseId : String? = null,
                    val canonicalBaseUrl : String? = null
                )
            }
        }
    }

    @Serializable
    data class ShortViewCountText(
        val simpleText: String? = null
    )

    @Serializable
    data class ThumbnailOverlay(
        val thumbnailOverlayTimeStatusRenderer : ThumbnailOverlayTimeStatusRenderer? = null
    ){
        @Serializable
        data class ThumbnailOverlayTimeStatusRenderer(
            val text : Text? = null
        ){
            @Serializable
            data class Text(
                val simpleText : String? = null
            )
        }
    }
}