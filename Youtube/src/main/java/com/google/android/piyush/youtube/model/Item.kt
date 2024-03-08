package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val contentDetails: ContentDetails? = null,
    val snippet: Snippet? = null,
    val kind: String? = null,
    val statistics: Statistics? = null
)