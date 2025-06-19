package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchBody(
    val context : Context,
    val query : String,
    val params : String? = null,
    val continuation : String? = null
)
