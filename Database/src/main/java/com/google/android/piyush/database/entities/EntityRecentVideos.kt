package com.google.android.piyush.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_videos")
data class EntityRecentVideos(
    @PrimaryKey val id : Int,
    val videoId: String?,
    val title: String?,
    val thumbnail: String?,
    val timing : String?,
    val channelId : String? = null,
)
