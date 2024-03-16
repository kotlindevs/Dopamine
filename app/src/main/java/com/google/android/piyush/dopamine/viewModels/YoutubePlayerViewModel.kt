package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.dopamine.activities.YoutubeChannel
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.model.channelPlaylists.ChannelPlaylists
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import kotlinx.coroutines.launch

class YoutubePlayerViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _videoDetails: MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val videoDetails: LiveData<YoutubeResource<Youtube>> = _videoDetails

    private val _channelDetails: MutableLiveData<YoutubeResource<com.google.android.piyush.youtube.model.channelDetails.YoutubeChannel>> =
        MutableLiveData()
    val channelDetails: MutableLiveData<YoutubeResource<com.google.android.piyush.youtube.model.channelDetails.YoutubeChannel>> =
        _channelDetails

    private val _channelsPlaylists: MutableLiveData<YoutubeResource<ChannelPlaylists>> =
        MutableLiveData()
    val channelsPlaylists: MutableLiveData<YoutubeResource<ChannelPlaylists>> = _channelsPlaylists

    fun getVideoDetails(videoId: String) {
        viewModelScope.launch {
            try {
                _videoDetails.postValue(YoutubeResource.Loading)
                val response = youtubeRepositoryImpl.getVideoDetails(videoId)
                if (response.items.isNullOrEmpty()) {
                    _videoDetails.postValue(
                        YoutubeResource.Error(
                            Exception(
                                "The request cannot be completed because you have exceeded your quota."
                            )
                        )
                    )
                } else {
                    _videoDetails.postValue(YoutubeResource.Success(response))
                }
            } catch (exception: Exception) {
                _videoDetails.postValue(YoutubeResource.Error(exception))
            }
        }
    }

    fun getChannelDetails(channelId: String) {
        viewModelScope.launch {
            try {
                _channelDetails.postValue(
                    YoutubeResource.Loading
                )
                val response = youtubeRepositoryImpl.getChannelDetails(channelId)
                if (response.items.isNullOrEmpty()) {
                    _channelDetails.postValue(
                        YoutubeResource.Error(
                            Exception(
                                "The request cannot be completed because server unreachable !"
                            )
                        )
                    )
                } else {
                    _channelDetails.postValue(
                        YoutubeResource.Success(
                            response
                        )
                    )
                }
            } catch (exception: Exception) {
                _channelDetails.postValue(
                    YoutubeResource.Error(
                        exception
                    )
                )
            }
        }
    }

    fun getChannelsPlaylist(channelId: String) {
        viewModelScope.launch {
            try {
                _channelsPlaylists.postValue(
                    YoutubeResource.Loading
                )
                val response = youtubeRepositoryImpl.getChannelsPlaylists(channelId)
                if (response.items.isNullOrEmpty()) {
                    _channelsPlaylists.postValue(
                        YoutubeResource.Error(
                            Exception(
                                "The request cannot be completed because you have exceeded your quota."
                            )
                        )
                    )
                } else {
                    _channelsPlaylists.postValue(
                        YoutubeResource.Success(
                            response
                        )
                    )
                }
            } catch (exception: Exception) {
                _channelsPlaylists.postValue(
                    YoutubeResource.Error(
                        exception
                    )
                )
            }
        }
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