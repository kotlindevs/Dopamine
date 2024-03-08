package com.google.android.piyush.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_playlist")
data class EntityFavouritePlaylist (
    @PrimaryKey val videoId: String,
    val title: String?,
    val customName: String?,
    val thumbnail: String?,
    val channelId : String? = null
)