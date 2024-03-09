package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import kotlinx.coroutines.launch

class YoutubeChannelPlaylistsVideosViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _playlistsVideos  : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val playlistsVideos : MutableLiveData<YoutubeResource<Youtube>> = _playlistsVideos

    fun getPlaylistsVideos(channelId: String) {
        viewModelScope.launch {
            try {
                _playlistsVideos.postValue(YoutubeResource.Loading)
                val response = youtubeRepositoryImpl.getPlaylistVideos(channelId)
                if(response.items.isNullOrEmpty()) {
                    _playlistsVideos.postValue(
                        YoutubeResource.Error(
                            Exception(
                                "The request cannot be completed because you have exceeded your quota."
                            )
                        )
                    )
                } else {
                    _playlistsVideos.postValue(
                        YoutubeResource.Success(response)
                    )
                }
            }catch (exception: Exception) {
                _playlistsVideos.postValue(
                    YoutubeResource.Error(exception)
                )
            }
        }
    }
}

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