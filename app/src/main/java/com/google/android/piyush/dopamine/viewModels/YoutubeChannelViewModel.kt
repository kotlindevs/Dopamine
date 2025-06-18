package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl

class YoutubeChannelViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

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