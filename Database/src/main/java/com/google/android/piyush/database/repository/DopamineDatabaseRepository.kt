package com.google.android.piyush.database.repository

import com.google.android.piyush.database.dao.DopamineDao
import com.google.android.piyush.database.entities.EntityFavouritePlaylist
import com.google.android.piyush.database.entities.EntityRecentVideos
import com.google.android.piyush.database.entities.EntityVideoSearch

class DopamineDatabaseRepository(
    private val dopamineDao: DopamineDao
) {
    suspend fun insertSearchVideos(vararg searchVideo: EntityVideoSearch) {
        dopamineDao.insertSearchVideos(*searchVideo)
    }

    suspend fun getSearchVideoList(): List<EntityVideoSearch> {
        return dopamineDao.getSearchVideoList()
    }

    suspend fun deleteSearchVideoList() {
        dopamineDao.deleteSearchVideoList()
    }

    suspend fun insertFavouriteVideos(vararg favouriteVideo: EntityFavouritePlaylist) {
        dopamineDao.insertFavouriteVideos(*favouriteVideo)
    }

    suspend fun isFavouriteVideo(videoId: String): List<EntityFavouritePlaylist> {
        return dopamineDao.isFavouriteVideo(videoId)
    }

    suspend fun deleteFavouriteVideo(videoId: String) {
        dopamineDao.deleteFavouriteVideo(videoId)
    }

    suspend fun getFavouritePlayList(): List<EntityFavouritePlaylist> {
        return dopamineDao.getFavouritePlayList()
    }

    suspend fun insertRecentVideos(vararg recentVideos: EntityRecentVideos) {
        dopamineDao.insertRecentVideos(*recentVideos)
    }

    suspend fun getRecentVideos(): List<EntityRecentVideos> {
        return dopamineDao.getRecentVideos()
    }

    suspend fun isRecentVideo(videoId: String): List<EntityRecentVideos> {
        return dopamineDao.isRecentVideo(videoId)
    }

    suspend fun updateRecentVideo(videoId: String, time: String) {
        dopamineDao.updateRecentVideo(videoId, time)
    }
}