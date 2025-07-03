package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class ChannelHomeHeader(
    val title : String?,
    val subtitle : String?
)

@Serializable
data class ChannelHomeContent(
    val header : ChannelHomeHeader,
    val items : MutableList<GridVideoRenderer>?
)