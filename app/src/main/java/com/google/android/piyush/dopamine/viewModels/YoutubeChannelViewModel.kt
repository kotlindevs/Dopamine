package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.youtube.model.channelDetails.YoutubeChannel
import com.google.android.piyush.youtube.model.channelPlaylists.ChannelPlaylists
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResponse

class YoutubeChannelViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _channelDetails : MutableLiveData<YoutubeResponse<YoutubeChannel>> = MutableLiveData()
    val channelDetails : MutableLiveData<YoutubeResponse<YoutubeChannel>> = _channelDetails

    private val _channelsPlaylists : MutableLiveData<YoutubeResponse<ChannelPlaylists>> = MutableLiveData()
    val channelsPlaylists : MutableLiveData<YoutubeResponse<ChannelPlaylists>> = _channelsPlaylists

    fun getChannelDetails(channelId : String) {
    }

    fun getChannelsPlaylist(channelId : String) {
    }
}


@Suppress("UNCHECKED_CAST")
class YoutubeChannelViewModelFactory(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       if(modelClass.isAssignableFrom(YoutubeChannelViewModel::class.java)) {
           return YoutubeChannelViewModel(youtubeRepositoryImpl) as T
       } else {
           throw IllegalArgumentException("Unknown ViewModel Class")
       }
    }
}