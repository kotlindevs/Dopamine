package com.google.android.piyush.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.android.piyush.database.entities.RecentSearch

@Dao
interface DopamineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSearchKeyword(recentSearch: RecentSearch)

    @Query("SELECT * FROM recent_search ORDER BY timestamp DESC LIMIT 5")
    suspend fun getSearchKeywords(): MutableList<RecentSearch>
}