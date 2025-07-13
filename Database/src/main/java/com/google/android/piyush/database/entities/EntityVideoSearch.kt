package com.google.android.piyush.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_table")
data class EntityVideoSearch(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER) val id: Int,
    @ColumnInfo(name = "search", typeAffinity = ColumnInfo.TEXT) val search: String?
)