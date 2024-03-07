package com.google.android.piyush.youtube.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.youtube.model.Shorts
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import kotlinx.coroutines.launch

class ShortsViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _shorts : MutableLiveData<List<Shorts>> = MutableLiveData()
    val shorts : LiveData<List<Shorts>> = _shorts

    init {
        viewModelScope.launch {
            val shortsResponse = youtubeRepositoryImpl.getYoutubeShorts()
            _shorts.value = shortsResponse
        }
    }
}

class ShortsViewModelFactory(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ShortsViewModel::class.java)){
            return ShortsViewModel(youtubeRepositoryImpl) as T
        }else{
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}