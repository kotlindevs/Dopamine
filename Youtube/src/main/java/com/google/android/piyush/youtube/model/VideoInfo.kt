package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoInfo(
    val videoId: String? = null,
    val title: String? = null,
    val publishedTime: String? = null,
    val viewCount: String? = null,
    val length: String? = null,
    val channelName: String? = null,
    val channelImage: String? = null,
    val description: String? = null
)