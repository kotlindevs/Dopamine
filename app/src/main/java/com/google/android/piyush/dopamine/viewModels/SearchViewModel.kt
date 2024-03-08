package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import kotlinx.coroutines.launch

class SearchViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _searchVideos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val searchVideos : MutableLiveData<YoutubeResource<Youtube>> = _searchVideos

    fun searchVideos(query : String) = viewModelScope.launch {
        try {
            _searchVideos.postValue(YoutubeResource.Loading)
            val videos = youtubeRepositoryImpl.getSearchVideos(query)
            _searchVideos.postValue(YoutubeResource.Success(videos))
        } catch (e : Exception) {
            _searchVideos.postValue(YoutubeResource.Error(e))
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