package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResponse

class YoutubeChannelPlaylistsVideosViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _playlistsVideos  : MutableLiveData<YoutubeResponse<Youtube>> = MutableLiveData()
    val playlistsVideos : MutableLiveData<YoutubeResponse<Youtube>> = _playlistsVideos

    fun getPlaylistsVideos(channelId: String) {
    }
}

@Suppress("UNCHECKED_CAST")
class YoutubeChannelPlaylistsViewModelFactory(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(YoutubeChannelPlaylistsVideosViewModel::class.java)) {
            return YoutubeChannelPlaylistsVideosViewModel(youtubeRepositoryImpl) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}