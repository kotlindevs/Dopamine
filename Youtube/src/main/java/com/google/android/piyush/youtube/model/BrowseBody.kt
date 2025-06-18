package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class BrowseBody(
    val context : Context,
    val browseId : String,
    val params : String? = null,
)
