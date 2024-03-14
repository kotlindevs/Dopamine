package com.google.android.piyush.database.model

data class CustomPlaylists (
    val videoId: String,
    val title: String?,
    val customName: String?,
    val thumbnail: String?,
    val channelId : String?,
    val publishedAt : String?,
    val viewCount : String?,
    val channelTitle : String?
)