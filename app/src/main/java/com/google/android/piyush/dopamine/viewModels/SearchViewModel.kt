package com.google.android.piyush.dopamine.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.youtube.model.SearchTube
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResponse

class SearchViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _searchVideos : MutableLiveData<YoutubeResponse<SearchTube>> = MutableLiveData()
    val searchVideos : MutableLiveData<YoutubeResponse<SearchTube>> = _searchVideos

    private val _reGetSearchVideos : MutableLiveData<YoutubeResponse<SearchTube>> = MutableLiveData()
    val reGetSearchVideos : MutableLiveData<YoutubeResponse<SearchTube>> = _reGetSearchVideos

    fun searchVideos(query : String) {

    }

    fun reSearchVideos(query : String) {
    }
}


@Suppress("UNCHECKED_CAST")
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