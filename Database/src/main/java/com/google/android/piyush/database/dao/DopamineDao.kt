package com.google.android.piyush.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.google.android.piyush.database.entities.EntityFavouritePlaylist
import com.google.android.piyush.database.entities.EntityRecentVideos
import com.google.android.piyush.database.entities.EntityVideoSearch

@Dao
interface DopamineDao {
    @Insert
    suspend fun insertSearchVideos(vararg searchVideo: EntityVideoSearch)
    @Query("SELECT * FROM search_table")
    suspend fun getSearchVideoList(): List<EntityVideoSearch>
    @Query("DELETE FROM search_table")
    suspend fun deleteSearchVideoList()
    @Insert
    suspend fun insertFavouriteVideos(vararg favouriteVideo : EntityFavouritePlaylist)
    @Query("SELECT videoId FROM favourite_playlist WHERE videoId = :videoId")
    suspend fun isFavouriteVideo (videoId : String) : String

    @Query("DELETE FROM favourite_playlist WHERE videoId = :videoId")
    suspend fun deleteFavouriteVideo(videoId : String)
    @Query("Select * FROM favourite_playlist")
    suspend fun getFavouritePlayList(): List<EntityFavouritePlaylist>
    @Insert
    suspend fun insertRecentVideos(vararg fav: EntityRecentVideos)
    @Query("Select * FROM recent_videos")
    suspend fun getRecentVideos(): List<EntityRecentVideos>
    @Query("SELECT videoId FROM recent_videos WHERE videoId = :videoId")
    suspend fun  isRecentVideo(videoId : String) : String
    @Query("Update recent_videos SET timing = :time WHERE videoId = :videoId")
    suspend fun updateRecentVideo(videoId: String, time: String)

}