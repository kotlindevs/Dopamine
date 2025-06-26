package com.google.android.piyush.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_search")
data class RecentSearch (
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val searchText : String,
    val timestamp : Long
)