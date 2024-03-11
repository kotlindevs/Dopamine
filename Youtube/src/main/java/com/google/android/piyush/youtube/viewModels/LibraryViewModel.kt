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
    private val _codingVideos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val codingVideos : LiveData<YoutubeResource<Youtube>> = _codingVideos

    private val _sportsVideos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val sportsVideos : LiveData<YoutubeResource<Youtube>> = _sportsVideos

    private val _technologyVideos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val technologyVideos : LiveData<YoutubeResource<Youtube>> = _technologyVideos

    private val _reGetCodingVideos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val reGetCodingVideos : LiveData<YoutubeResource<Youtube>> = _reGetCodingVideos

    private val _reGetSportsVideos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val reGetSportsVideos : LiveData<YoutubeResource<Youtube>> = _reGetSportsVideos

    private val _reGetTechnologyVideos : MutableLiveData<YoutubeResource<Youtube>> = MutableLiveData()
    val reGetTechnologyVideos : LiveData<YoutubeResource<Youtube>> = _reGetTechnologyVideos
    init {
        getCodingVideos()
        getSportsVideos()
        getTechnologyVideos()
        reGetCodingVideos()
        reGetSportsVideos()
        reGetTechnologyVideos()
    }

    private fun getCodingVideos() = viewModelScope.launch {
        try {
            _codingVideos.postValue(YoutubeResource.Loading)
            val response = youtubeRepositoryImpl.getLibraryVideos(
                YoutubeClient.CODING_VIDEOS
            )
            if(response.items.isNullOrEmpty()){
                _codingVideos.postValue(
                    YoutubeResource.Error(
                        Exception(
                            "The request cannot be completed because you have exceeded your quota."
                        )
                    )
                )
            }else{
                _codingVideos.postValue(
                    YoutubeResource.Success(response)
                )
            }
        }catch (exception : Exception){
            _codingVideos.postValue(
                YoutubeResource.Error(exception)
            )
        }
    }
    private fun getSportsVideos() = viewModelScope.launch {
        try {
            _sportsVideos.postValue(YoutubeResource.Loading)
            val response = youtubeRepositoryImpl.getLibraryVideos(
                YoutubeClient.SPORTS_VIDEOS
            )
            if(response.items.isNullOrEmpty()){
                _sportsVideos.postValue(
                    YoutubeResource.Error(
                        Exception(
                            "The request cannot be completed because you have exceeded your quota."
                        )
                    )
                )
            }else{
                _sportsVideos.postValue(
                    YoutubeResource.Success(response)
                )
            }
        }catch (exception : Exception){
            _sportsVideos.postValue(
                YoutubeResource.Error(exception)
            )
        }
    }

    private fun getTechnologyVideos() = viewModelScope.launch {
        try {
            _technologyVideos.postValue(YoutubeResource.Loading)
            val response = youtubeRepositoryImpl.getLibraryVideos(
                YoutubeClient.TECH_VIDEOS
            )
            if(response.items.isNullOrEmpty()){
                _technologyVideos.postValue(
                    YoutubeResource.Error(
                        Exception(
                            "The request cannot be completed because you have exceeded your quota."
                        )
                    )
                )
            }else{
                _technologyVideos.postValue(
                    YoutubeResource.Success(response)
                )
            }
        }catch (exception : Exception){
            _technologyVideos.postValue(
                YoutubeResource.Error(exception)
            )
        }
    }

    private fun reGetCodingVideos() = viewModelScope.launch {
            try {
                _reGetCodingVideos.postValue(YoutubeResource.Loading)
                _reGetCodingVideos.postValue(
                    YoutubeResource.Success(
                        youtubeRepositoryImpl.reGetLibraryVideos(
                            YoutubeClient.CODING_VIDEOS
                        )
                    )
                )
            }catch (exception : Exception){
                _reGetCodingVideos.postValue(
                    YoutubeResource.Error(exception)
                )
            }
    }

    private fun reGetSportsVideos() = viewModelScope.launch {
            try {
                _reGetSportsVideos.postValue(YoutubeResource.Loading)
                _reGetSportsVideos.postValue(
                    YoutubeResource.Success(
                        youtubeRepositoryImpl.reGetLibraryVideos(
                            YoutubeClient.SPORTS_VIDEOS
                        )
                    )
                )
            }catch (exception : Exception){
                _reGetSportsVideos.postValue(
                    YoutubeResource.Error(exception)
                )
            }

    }

    private fun reGetTechnologyVideos() = viewModelScope.launch {
        try {
            _reGetTechnologyVideos.postValue(YoutubeResource.Loading)
            _reGetTechnologyVideos.postValue(
                YoutubeResource.Success(
                    youtubeRepositoryImpl.reGetLibraryVideos(
                        YoutubeClient.TECH_VIDEOS
                    )
                )
            )
        }catch (exception : Exception){
            _reGetTechnologyVideos.postValue(
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