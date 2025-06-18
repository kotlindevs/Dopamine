package com.google.android.piyush.youtube.repository

import com.google.android.piyush.youtube.model.BrowseResponse

interface YoutubeRepository {
    suspend fun browseNow(browseId : String) : BrowseResponse
    suspend fun browseMusic(browseId : String) : BrowseResponse
    suspend fun browseGaming(browseId : String) : BrowseResponse
    suspend fun browseMovies(browseId : String) : BrowseResponse
}