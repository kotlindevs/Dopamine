package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchTubeItems(
    val contentDetails: ContentDetails? = null,
    val snippet: Snippet? = null,
    val id: Id? = null,
    val kind: String? = null,
    val statistics: Statistics? = null
)

@Serializable
data class Id(
    val kind: String? = null,
    val videoId: String? = null
)