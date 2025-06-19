package com.google.android.piyush.youtube.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.youtube.TRENDING
import com.google.android.piyush.youtube.model.BrowseResponse
import com.google.android.piyush.youtube.model.PlayerResponse
import com.google.android.piyush.youtube.model.VideoInfo
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResponse
import kotlinx.coroutines.launch

class YoutubeViewModel() : ViewModel() {

    private val repository : YoutubeRepositoryImpl = YoutubeRepositoryImpl()

    private val _trendingVideos : MutableLiveData<YoutubeResponse<BrowseResponse>> = MutableLiveData()
    val trendingVideos : LiveData<YoutubeResponse<BrowseResponse>> = _trendingVideos

    private val _musicVideos : MutableLiveData<YoutubeResponse<BrowseResponse>> = MutableLiveData()
    val musicVideos : LiveData<YoutubeResponse<BrowseResponse>> = _musicVideos

    private val _gamingVideos : MutableLiveData<YoutubeResponse<BrowseResponse>> = MutableLiveData()
    val gamingVideos : LiveData<YoutubeResponse<BrowseResponse>> = _gamingVideos

    private val _moviesVideos : MutableLiveData<YoutubeResponse<BrowseResponse>> = MutableLiveData()
    val moviesVideos : LiveData<YoutubeResponse<BrowseResponse>> = _moviesVideos

    private val _playerInfo : MutableLiveData<YoutubeResponse<PlayerResponse>> = MutableLiveData()
    val playerInfo : LiveData<YoutubeResponse<PlayerResponse>> = _playerInfo

    private val _sharedVideoInfo : MutableLiveData<YoutubeResponse<VideoInfo>> = MutableLiveData()
    val sharedVideoInfo : LiveData<YoutubeResponse<VideoInfo>> = _sharedVideoInfo

    private var alreadyExistsData = false

    init {
        getTrendingVideos()
    }

    fun submitSharedVideoInfo(videoInfo: VideoInfo) {
        try {
            _sharedVideoInfo.postValue(YoutubeResponse.Success(videoInfo))
        } catch (e : Exception) {
            _sharedVideoInfo.postValue(YoutubeResponse.Error(e))
        }
    }

    fun getPlayerInfo(videoId : String) = viewModelScope.launch {
        _playerInfo.postValue(YoutubeResponse.Loading)
        try {
            val info = repository.playerInfo(videoId)
            _playerInfo.postValue(YoutubeResponse.Success(info))
        } catch (e : Exception) {
            _playerInfo.postValue(YoutubeResponse.Error(e))
        }
    }

    private fun getTrendingVideos() = viewModelScope.launch {
       if(!alreadyExistsData){
           _trendingVideos.postValue(YoutubeResponse.Loading)
           _moviesVideos.postValue(YoutubeResponse.Loading)
           _gamingVideos.postValue(YoutubeResponse.Loading)
           _musicVideos.postValue(YoutubeResponse.Loading)
           try {
               val now = repository.browseNow(TRENDING)
               val music = repository.browseMusic(TRENDING)
               val gaming = repository.browseGaming(TRENDING)
               val movies = repository.browseMovies(TRENDING)
               _trendingVideos.postValue(YoutubeResponse.Success(now))
               _musicVideos.postValue(YoutubeResponse.Success(music))
               _gamingVideos.postValue(YoutubeResponse.Success(gaming))
               _moviesVideos.postValue(YoutubeResponse.Success(movies))
               alreadyExistsData = true
           } catch (e : Exception) {
               _trendingVideos.postValue(YoutubeResponse.Error(e))
               _musicVideos.postValue(YoutubeResponse.Error(e))
               _gamingVideos.postValue(YoutubeResponse.Error(e))
               _moviesVideos.postValue(YoutubeResponse.Error(e))
           }
       }
    }
}

