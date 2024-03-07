package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class ContentDetails(
    val duration: String? = null,
    val videoPublishedAt: String? = null,
    val videoId: String? = null
)
