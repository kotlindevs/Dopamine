package com.google.android.piyush.youtube.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl

class LibraryViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {
}

@Suppress("UNCHECKED_CAST")
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