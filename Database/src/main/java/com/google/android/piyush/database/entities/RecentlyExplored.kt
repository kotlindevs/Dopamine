package com.google.android.piyush.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("recently_explored")
data class RecentlyExplored(
    @PrimaryKey val videoId : String,
    val thumbnail : String? = null,
    val title : String? = null,
    val longBylineText : String? = null,
    val lengthText : String? = null,
    val publishedTimeText : String? = null,
    val shortViewCountText : String? = null,
    val avatar : String? = null
)
