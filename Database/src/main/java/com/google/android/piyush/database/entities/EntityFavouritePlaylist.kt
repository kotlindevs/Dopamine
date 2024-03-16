package com.google.android.piyush.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_playlist")
data class EntityFavouritePlaylist (
    @PrimaryKey val videoId: String,
    val title: String?,
    val thumbnail: String?,
    val channelTitle: String?,
    val channelId : String? = null
)