package com.google.android.piyush.youtube.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class LockupViewModel(
    val contentImage : ContentImage? = null,
    val metadata : Metadata? = null,
    val contentId : String? = null
){
    @Serializable
    data class ContentImage(
        val collectionThumbnailViewModel : CollectionThumbnailViewModel? = null
    ){
        @Serializable
        data class CollectionThumbnailViewModel(
            val primaryThumbnail : PrimaryThumbnail? = null
        ){
            @Serializable
            data class PrimaryThumbnail(
                val thumbnailViewModel : ThumbnailViewModel? = null
            ){
                @Serializable
                data class ThumbnailViewModel(
                    val image : Image? = null,
                    val overlays : List<Overlay>? = null
                ){
                    @Serializable
                    data class Image(
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
                    data class Overlay(
                        val thumbnailOverlayBadgeViewModel : ThumbnailOverlayBadgeViewModel? = null
                    ){
                        @Serializable
                        data class ThumbnailOverlayBadgeViewModel(
                            val thumbnailBadges : List<ThumbnailBadge>? = null
                        ){
                            @Serializable
                            data class ThumbnailBadge(
                                val thumbnailBadgeViewModel : ThumbnailBadgeViewModel? = null
                            ){
                                @Serializable
                                data class ThumbnailBadgeViewModel(
                                    val text : String? = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Serializable
    data class Metadata(
        val lockupMetadataViewModel : LockupMetadataViewModel? = null
    ){
        @Serializable
        data class LockupMetadataViewModel(
            val title : Title? = null
        ){
            @Serializable
            data class Title(
                val content : String? = null
            )
        }
    }
}
