package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class Youtube(
    val regionCode: String? = null,
    val items: List<Item>? = null
)