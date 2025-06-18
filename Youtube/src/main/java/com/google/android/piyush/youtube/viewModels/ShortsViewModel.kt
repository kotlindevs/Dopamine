package com.google.android.piyush.youtube.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.youtube.model.Shorts
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResponse

class ShortsViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _shorts : MutableLiveData<YoutubeResponse<List<Shorts>>> = MutableLiveData()
    val shorts : LiveData<YoutubeResponse<List<Shorts>>> = _shorts
}

class ShortsViewModelFactory(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ShortsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ShortsViewModel(youtubeRepositoryImpl) as T
        }else{
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}