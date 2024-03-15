package com.google.android.piyush.youtube.model.channelDetails

import kotlinx.serialization.Serializable

@Serializable
data class YoutubeChannel(
    val items : List<Item>? = null
)

@Serializable
data class Item(
    val kind : String? = null,
    val id : String? = null,
    val snippet : Snippet? = null,
    val statistics : Statistics? = null,
    val brandingSettings : BrandingSettings? = null,
)


@Serializable
data class Snippet(
    val title : String? = null,
    val description : String? = null,
    val customUrl : String? = null,
    val publishedAt : String? = null,
    val thumbnails : Thumbnails? = null,
    val country : String? = null,
)

@Serializable
data class Thumbnails(
    val default : Default? = null,
)

@Serializable
data class Default(
    val url : String? = null,
    val width : Int? = null,
    val height : Int? = null
)

@Serializable
data class Statistics(
    val viewCount: String? = null,
    val subscriberCount: String? = null,
    val likeCount: String? = null
)

@Serializable
data class BrandingSettings(
    val image : Image? = null
)

@Serializable
data class Image(
    val bannerExternalUrl : String? = null
)
