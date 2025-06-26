package com.google.android.piyush.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.google.android.piyush.database.entities.RecentSearch

@Dao
interface DopamineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSearchKeyword(recentSearch: RecentSearch)
}