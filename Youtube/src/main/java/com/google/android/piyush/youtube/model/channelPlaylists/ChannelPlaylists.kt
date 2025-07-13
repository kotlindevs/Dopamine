package com.google.android.piyush.youtube.model.channelPlaylists

import kotlinx.serialization.Serializable

@Serializable
data class ChannelPlaylists(
    val nextPageToken: String? = null,
    val pageInfo: PageInfo? = null,
    val items: List<Item>? = null
)

@Serializable
data class Item(
    val id: String? = null,
    val snippet: Snippet? = null,
    val contentDetails: ContentDetails? = null
)

@Serializable
data class PageInfo(
    val totalResults: Int? = null,
    val resultsPerPage: Int? = null
)

@Serializable
data class Snippet(
    val publishedAt: String? = null,
    val channelId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val thumbnails: Thumbnails? = null,
    val channelTitle: String? = null,
)

@Serializable
data class ContentDetails(
    val itemCount: Int? = null
)

@Serializable
data class Thumbnails(
    val default: Default? = null,
    val medium: Medium? = null,
    val high: High? = null
)

@Serializable
data class Default(
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null
)

@Serializable
data class Medium(
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null
)

@Serializable
data class High(
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null
)