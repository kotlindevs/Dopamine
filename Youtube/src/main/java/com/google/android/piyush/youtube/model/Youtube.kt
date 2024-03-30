package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class Youtube(
    val regionCode: String? = null,
    val items: List<Item>? = null,
    val nextPageToken : String? = null,
    val pageInfo : PageInfo? = null
)

@Serializable
data class PageInfo(
    val totalResults : Int? = null,
    val resultsPerPage : Int? = null
)