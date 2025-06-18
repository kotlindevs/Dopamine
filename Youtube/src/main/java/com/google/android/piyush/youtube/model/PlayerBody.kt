package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerBody(
    val context : Context,
    val videoId : String
)
