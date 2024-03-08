package com.google.android.piyush.youtube.repository

import com.google.android.piyush.youtube.model.Shorts
import com.google.android.piyush.youtube.model.Youtube

interface YoutubeRepository {
    suspend fun getHomeVideos() : Youtube
    suspend fun getLibraryVideos(playListId : String) : Youtube
    suspend fun getSearchVideos(query : String) : Youtube
    suspend fun getYoutubeShorts() : List<Shorts>
}