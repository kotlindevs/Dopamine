package com.google.android.piyush.youtube.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeClient
import com.google.android.piyush.youtube.utilities.YoutubeResource
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {
    private val _reverbAndSlowedVideos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val reverbAndSlowedVideos : LiveData<YoutubeResource<Youtube>> = _reverbAndSlowedVideos

    private val _gamingVideos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val gamingVideos : LiveData<YoutubeResource<Youtube>> = _gamingVideos

    private val _lofiBhajan : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val lofiBhajan : LiveData<YoutubeResource<Youtube>> = _lofiBhajan
    init {
        getReverbAndSlowedVideo()
        getGamingVideos()
        getLofiBhajan()
    }

    private fun getReverbAndSlowedVideo() = viewModelScope.launch {
        try {
            _reverbAndSlowedVideos.postValue(YoutubeResource.Loading)
            val response = youtubeRepositoryImpl.getLibraryVideos(
                YoutubeClient.REVERB_AND_SLOWED
            )
            if(response.items.isNullOrEmpty()){
                _reverbAndSlowedVideos.postValue(
                    YoutubeResource.Error(
                        Exception(
                            "The request cannot be completed because you have exceeded your quota."
                        )
                    )
                )
            }else{
                _reverbAndSlowedVideos.postValue(
                    YoutubeResource.Success(response)
                )
            }
        }catch (exception : Exception){
            _reverbAndSlowedVideos.postValue(
                YoutubeResource.Error(exception)
            )
        }
    }

    private fun getGamingVideos() = viewModelScope.launch {
        try {
            _gamingVideos.postValue(YoutubeResource.Loading)
            val response = youtubeRepositoryImpl.getLibraryVideos(
                YoutubeClient.GAMING_VIDEOS
            )
            if(response.items.isNullOrEmpty()){
                _gamingVideos.postValue(
                    YoutubeResource.Error(
                        Exception(
                            "The request cannot be completed because you have exceeded your quota."
                        )
                    )
                )
            }else{
                _gamingVideos.postValue(
                    YoutubeResource.Success(response)
                )
            }
        }catch (exception : Exception){
            _gamingVideos.postValue(
                YoutubeResource.Error(exception)
            )
        }
    }

    private fun getLofiBhajan() = viewModelScope.launch {
        try {
            _lofiBhajan.postValue(YoutubeResource.Loading)
            val response = youtubeRepositoryImpl.getLibraryVideos(
                YoutubeClient.LOFI_BHAJAN
            )
            if(response.items.isNullOrEmpty()){
                _lofiBhajan.postValue(
                    YoutubeResource.Error(
                        Exception(
                            "The request cannot be completed because you have exceeded your quota."
                        )
                    )
                )
            }else{
                _lofiBhajan.postValue(
                    YoutubeResource.Success(response)
                )
            }
        }catch (exception : Exception){
            _lofiBhajan.postValue(
                YoutubeResource.Error(exception)
            )
        }
    }
}

class LibraryViewModelFactory(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)){
            return LibraryViewModel(youtubeRepositoryImpl) as T
        }else{
            throw IllegalArgumentException("Unknown class name")
        }
    }
}