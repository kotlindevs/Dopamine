package com.google.android.piyush.youtube.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import kotlinx.coroutines.launch

class HomeViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _videos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val videos : LiveData<YoutubeResource<Youtube>> = _videos

    private val _advanceVideos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val advanceVideos : LiveData<YoutubeResource<Youtube>> = _advanceVideos

    init {
        getHomeVideos()
    }

    private fun getHomeVideos() = viewModelScope.launch {
        try {
            _videos.postValue(
                YoutubeResource.Loading
            )
            val response = youtubeRepositoryImpl.getHomeVideos()
            if(response.items.isNullOrEmpty()){
                _videos.postValue(
                    YoutubeResource.Error(
                        Exception(
                            "The request cannot be completed because you have exceeded your quota."
                        )
                    )
                )
            }else{
                _videos.postValue(
                    YoutubeResource.Success(
                        response
                    )
                )
            }
        }catch (exception : Exception){
            _videos.postValue(
                YoutubeResource.Error(
                    exception = exception
                )
            )
            exception.printStackTrace()
        }
    }

    fun getAdvanceVideos() {
        viewModelScope.launch {
            try {
                _advanceVideos.postValue(
                    YoutubeResource.Loading
                )
                val response = youtubeRepositoryImpl.advanceHome()
                if(response.items.isNullOrEmpty()) {
                    _advanceVideos.postValue(
                        YoutubeResource.Error(
                            Exception(
                                "The request cannot be completed because you have exceeded your quota."
                            )
                        )
                    )
                }else{
                    _advanceVideos.postValue(
                        YoutubeResource.Success(
                            response
                        )
                    )
                }
            }catch (exception : Exception){
                _advanceVideos.postValue(
                    YoutubeResource.Error(
                        exception = exception
                    )
                )
            }
        }
    }
}

class HomeViewModelFactory(
    private val repository: YoutubeRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException(
            "Unknown class name"
        )
    }
}