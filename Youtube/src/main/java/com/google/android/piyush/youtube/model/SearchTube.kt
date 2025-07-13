package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchTube(
    val regionCode: String? = null,
    val items: List<SearchTubeItems>? = null
)