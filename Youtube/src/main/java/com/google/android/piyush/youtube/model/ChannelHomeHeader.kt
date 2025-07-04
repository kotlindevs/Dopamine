package com.google.android.piyush.youtube.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class ChannelHomeHeader(
    val title : String?,
    val subtitle : String?
)

@InternalSerializationApi
@Serializable
data class ChannelHomeContent(
    val header : ChannelHomeHeader,
    val videos : MutableList<GridVideoRenderer>?,
    val channels : MutableList<GridChannelRenderer>?
)