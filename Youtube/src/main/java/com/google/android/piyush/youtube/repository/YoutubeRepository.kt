package com.google.android.piyush.youtube.repository

import com.google.android.piyush.youtube.model.SearchTube
import com.google.android.piyush.youtube.model.Shorts
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.model.channelDetails.YoutubeChannel
import com.google.android.piyush.youtube.model.channelPlaylists.ChannelPlaylists

interface YoutubeRepository {
    suspend fun getHomeVideos() : Youtube
    suspend fun getLibraryVideos(playListId : String) : Youtube
    suspend fun getSearchVideos(query : String) : SearchTube
    suspend fun getYoutubeShorts() : List<Shorts>
    suspend fun getChannelDetails(channelId : String) : YoutubeChannel
    suspend fun getChannelsPlaylists(channelId : String) : ChannelPlaylists
    suspend fun getPlaylistVideos(playListId : String) : Youtube
    suspend fun getVideoDetails(videoId : String) : Youtube
}