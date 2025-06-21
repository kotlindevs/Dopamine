package com.google.android.piyush.youtube.repository

import com.google.android.piyush.youtube.model.BrowseResponse
import com.google.android.piyush.youtube.model.PlayerResponse
import com.google.android.piyush.youtube.model.SearchResponse
import com.google.android.piyush.youtube.model.SearchSuggestions

interface YoutubeRepository {
    suspend fun browseNow(browseId : String) : BrowseResponse
    suspend fun browseMusic(browseId : String) : BrowseResponse
    suspend fun browseGaming(browseId : String) : BrowseResponse
    suspend fun browseMovies(browseId : String) : BrowseResponse
    suspend fun playerInfo(videoId : String) : PlayerResponse
    suspend fun searchResults(query : String) : SearchResponse
    suspend fun searchSuggestions(query : String) : SearchSuggestions
}