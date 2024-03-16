package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.youtube.model.SearchTube
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import kotlinx.coroutines.launch

class SearchViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _searchVideos : MutableLiveData<YoutubeResource<SearchTube>> = MutableLiveData()
    val searchVideos : MutableLiveData<YoutubeResource<SearchTube>> = _searchVideos

    private val _reGetSearchVideos : MutableLiveData<YoutubeResource<SearchTube>> = MutableLiveData()
    val reGetSearchVideos : MutableLiveData<YoutubeResource<SearchTube>> = _reGetSearchVideos

    fun searchVideos(query : String) {
        viewModelScope.launch {
            try {
                _searchVideos.postValue(YoutubeResource.Loading)
                val videos = youtubeRepositoryImpl.getSearchVideos(query)
                if(videos.items.isNullOrEmpty()){
                    _searchVideos.postValue(
                        YoutubeResource.Error(
                            Exception(
                                "The request cannot be completed because you have exceeded your quota."
                            )
                        )
                    )
                } else {
                    _searchVideos.postValue(YoutubeResource.Success(videos))
                }
            } catch (e : Exception) {
                _searchVideos.postValue(YoutubeResource.Error(e))
            }
        }
    }

    fun reSearchVideos(query : String) {
        viewModelScope.launch {
            try {
                _reGetSearchVideos.postValue(YoutubeResource.Loading)
                val videos = youtubeRepositoryImpl.reGetSearchVideos(query)
                if(videos.items.isNullOrEmpty()){
                    _reGetSearchVideos.postValue(
                        YoutubeResource.Error(
                            Exception(
                                "The request cannot be completed because you have exceeded your quota."
                            )
                        )
                    )
                } else {
                    _reGetSearchVideos.postValue(YoutubeResource.Success(videos))
                }
            } catch (e : Exception) {
                _reGetSearchVideos.postValue(YoutubeResource.Error(e))
            }
        }
    }
}


class SearchViewModelFactory(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(youtubeRepositoryImpl) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}