package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.model.channelPlaylists.ChannelPlaylists
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResponse

class YoutubePlayerViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _videoDetails: MutableLiveData<YoutubeResponse<Youtube>> = MutableLiveData()
    val videoDetails: LiveData<YoutubeResponse<Youtube>> = _videoDetails

    private val _channelDetails: MutableLiveData<YoutubeResponse<com.google.android.piyush.youtube.model.channelDetails.YoutubeChannel>> =
        MutableLiveData()
    val channelDetails: MutableLiveData<YoutubeResponse<com.google.android.piyush.youtube.model.channelDetails.YoutubeChannel>> =
        _channelDetails

    private val _channelsPlaylists: MutableLiveData<YoutubeResponse<ChannelPlaylists>> =
        MutableLiveData()
    val channelsPlaylists: MutableLiveData<YoutubeResponse<ChannelPlaylists>> = _channelsPlaylists

    fun getVideoDetails(videoId: String) {

    }

    fun getChannelDetails(channelId: String) {
    }

    fun getChannelsPlaylist(channelId: String) {

    }
}

class YoutubePlayerViewModelFactory(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(YoutubePlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return YoutubePlayerViewModel(youtubeRepositoryImpl) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}