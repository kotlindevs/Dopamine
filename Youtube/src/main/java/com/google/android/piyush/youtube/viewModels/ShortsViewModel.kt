package com.google.android.piyush.youtube.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.youtube.model.Shorts
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import kotlinx.coroutines.launch

class ShortsViewModel(
    private val youtubeRepositoryImpl: YoutubeRepositoryImpl
) : ViewModel() {

    private val _shorts : MutableLiveData<YoutubeResource<List<Shorts>>> = MutableLiveData()
    val shorts : LiveData<YoutubeResource<List<Shorts>>> = _shorts

    init {
        viewModelScope.launch {
            try {
                _shorts.postValue(YoutubeResource.Loading)
                val response = youtubeRepositoryImpl.getYoutubeShorts()
                if(response.isEmpty()){
                    _shorts.postValue(
                        YoutubeResource.Error(
                            Exception("No Shorts Found")
                        )
                    )
                }else{
                    _shorts.postValue(YoutubeResource.Success(response))
                }
            }catch (e : Exception){
                _shorts.postValue(YoutubeResource.Error(e))
            }
        }
    }
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