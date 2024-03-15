package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class Snippet(
    val channelId: String? = null,
    val channelTitle: String? = null,
    val customUrl: String? = null,
    val thumbnails: Thumbnails? = null,
    val publishedAt: String? = null,
    val kind: String? = null,
    val description: String? = null,
    val tags: List<String>? = null,
    val title: String? = null
)


@Serializable
data class Thumbnails(
    val default: Default? = null,
    val medium: Medium? = null,
    val high: High? = null,
    val standard: Standard? = null
)

@Serializable
data class Statistics(
    val viewCount: Int? = 0,
    val subscriberCount: Int? = 0,
    val likeCount: Int? = 0
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

@Serializable
data class Medium(
    val url: String? = null,
)