package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.model.channelDetails.YoutubeChannel
import com.google.android.piyush.youtube.model.channelPlaylists.ChannelPlaylists
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import kotlinx.coroutines.launch

class YoutubeChannelViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _channelDetails : MutableLiveData<YoutubeResource<YoutubeChannel>> = MutableLiveData()
    val channelDetails : MutableLiveData<YoutubeResource<YoutubeChannel>> = _channelDetails

    private val _channelsPlaylists : MutableLiveData<YoutubeResource<ChannelPlaylists>> = MutableLiveData()
    val channelsPlaylists : MutableLiveData<YoutubeResource<ChannelPlaylists>> = _channelsPlaylists

    fun getChannelDetails(channelId : String) {
       viewModelScope.launch {
           try {
               _channelDetails.postValue(
                   YoutubeResource.Loading
               )
               val response = youtubeRepositoryImpl.getChannelDetails(channelId)
               if(response.items.isNullOrEmpty()) {
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
           }catch (exception : Exception) {
               _channelDetails.postValue(
                   YoutubeResource.Error(
                       exception
                   )
               )
           }
       }
    }

    fun getChannelsPlaylist(channelId : String) {
        viewModelScope.launch {
            try{
                _channelsPlaylists.postValue(
                    YoutubeResource.Loading
                )
                val response = youtubeRepositoryImpl.getChannelsPlaylists(channelId)
                if(response.items.isNullOrEmpty()) {
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
            }catch (exception : Exception) {
                _channelsPlaylists.postValue(
                    YoutubeResource.Error(
                        exception
                    )
                )
            }
        }
    }
}


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