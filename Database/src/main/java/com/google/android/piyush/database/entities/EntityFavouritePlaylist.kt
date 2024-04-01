package com.google.android.piyush.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_playlist")
data class EntityFavouritePlaylist (
    @PrimaryKey val videoId: String,
    val title: String? = null,
    val thumbnail: String? = null,
    val channelId : String? = null,
    val publishedAt : String? = null,
    val viewCount : String? = null,
    val channelTitle : String? = null,
    val duration : String? = null
)