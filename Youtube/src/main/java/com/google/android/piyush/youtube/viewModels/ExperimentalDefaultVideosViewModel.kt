package com.google.android.piyush.youtube.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.youtube.model.SearchTube
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import kotlinx.coroutines.launch

class ExperimentalDefaultVideosViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _defaultVideos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val defaultVideos : MutableLiveData<YoutubeResource<Youtube>> = _defaultVideos

    init { getDefaultVideos() }

    private fun getDefaultVideos() {
        viewModelScope.launch {
            try {
                _defaultVideos.postValue(YoutubeResource.Loading)
                val response = youtubeRepositoryImpl.experimentalDefaultVideos()
                if(response.items.isNullOrEmpty()) {
                    _defaultVideos.postValue(
                        YoutubeResource.Error(
                            Exception(
                                "You are using experimental api and it is not working properly use at your own risk !"
                            )
                        )
                    )
                } else {
                    _defaultVideos.postValue(YoutubeResource.Success(response))
                }
            } catch (e: Exception) {
                _defaultVideos.postValue(YoutubeResource.Error(e))
            }
        }
    }

    private val _searchVideos : MutableLiveData<YoutubeResource<SearchTube>> = MutableLiveData()
    val searchVideos : MutableLiveData<YoutubeResource<SearchTube>> = _searchVideos

    fun getSearchVideos(search : String) {
        viewModelScope.launch {
            try {
                _searchVideos.postValue(YoutubeResource.Loading)
                val response = youtubeRepositoryImpl.experimentalSearchVideos(search)
                if(response.items.isNullOrEmpty()){
                    _searchVideos.postValue(
                        YoutubeResource.Error(
                            Exception(
                                "This is experimental api and use at your own risk if you are not sure about it to contact developer"
                            )
                        )
                    )
                } else {
                    _searchVideos.postValue(YoutubeResource.Success(response))
                }
            } catch (e : Exception) {
                _searchVideos.postValue(YoutubeResource.Error(e))
            }
        }
    }
}


class ExperimentalVideosViewModelFactory(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ExperimentalDefaultVideosViewModel::class.java)) {
            return ExperimentalDefaultVideosViewModel(youtubeRepositoryImpl) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}