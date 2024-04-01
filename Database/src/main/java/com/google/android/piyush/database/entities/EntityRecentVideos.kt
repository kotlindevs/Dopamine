package com.google.android.piyush.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_videos")
data class EntityRecentVideos(
    @PrimaryKey val id : Int,
    val videoId: String? = null,
    val title: String? = null,
    val thumbnail: String? = null,
    val timing : String? = null,
    val channelId : String? = null,
    val length : String? = null
)
