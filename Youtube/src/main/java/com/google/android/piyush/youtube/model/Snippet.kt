package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class Snippet(
    val channelId: String? = null,
    val channelTitle: String? = null,
    val thumbnails: Thumbnails? = null,
    val publishedAt: String? = null,
    val title: String? = null
)


@Serializable
data class Thumbnails(
    val default: Default? = null,
    val high: High? = null,
    val standard: Standard? = null
)

@Serializable
data class Statistics(
    val viewCount: String? = null
)

@Serializable
data class Standard(
    val url: String? = null,
)

@Serializable
data class High(
    val url: String? = null,
)

@Serializable
data class Default(
    val url: String? = null,
)