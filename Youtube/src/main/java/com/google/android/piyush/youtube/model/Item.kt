package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val contentDetails: ContentDetails? = null,
    val id: String? = null,
    val snippet: Snippet? = null,
    val statistics: Statistics? = null
)