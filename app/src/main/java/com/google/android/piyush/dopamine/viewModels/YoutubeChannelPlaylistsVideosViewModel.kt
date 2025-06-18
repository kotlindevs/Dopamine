package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl

class YoutubeChannelPlaylistsVideosViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {
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