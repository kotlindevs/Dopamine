package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchSuggestions(
    val refinements : List<String>? = null
)
