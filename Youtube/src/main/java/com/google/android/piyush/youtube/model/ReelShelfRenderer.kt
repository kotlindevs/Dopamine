package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class ReelShelfRenderer(
    val title : Title? = null,
    val items : List<Item>? = null
){
    @Serializable
    data class Title(
        val simpleText : String? = null
    )
    @Serializable
    data class Item(
        val shortsLockupViewModel : ShortsLockupViewModel? = null
    ){
        @Serializable
        data class ShortsLockupViewModel(
            val thumbnail : Thumbnail? = null,
            val overlayMetadata : OverlayMetadata? = null,
            val onTap : OnTap? = null
        ){
            @Serializable
            data class OnTap(
                val innertubeCommand : InnertubeCommand? = null
            ){
                @Serializable
                data class InnertubeCommand(
                    val reelWatchEndpoint : ReelWatchEndpoint? = null
                ){
                    @Serializable
                    data class ReelWatchEndpoint(
                        val videoId : String? = null
                    )
                }
            }
            @Serializable
            data class Thumbnail(
                val sources : List<Source>? = null
            ){
                @Serializable
                data class Source(
                    val url : String? = null,
                    val width : Int? = null,
                    val height : Int? = null
                )
            }
            @Serializable
            data class OverlayMetadata(
                val primaryText : PrimaryText? = null,
                val secondaryText : SecondaryText? = null
            ){
                @Serializable
                data class PrimaryText(
                    val content : String? = null
                )
                @Serializable
                data class SecondaryText(
                    val content : String? = null
                )
            }
        }
    }
}