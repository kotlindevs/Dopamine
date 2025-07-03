package com.google.android.piyush.dopamine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.youtube.CHANNEL_HOME
import com.google.android.piyush.youtube.TRENDING
import com.google.android.piyush.youtube.model.BrowseResponse
import com.google.android.piyush.youtube.model.ChannelResponse
import com.google.android.piyush.youtube.model.PlayerResponse
import com.google.android.piyush.youtube.model.ReelShelfRenderer
import com.google.android.piyush.youtube.model.SearchResponse
import com.google.android.piyush.youtube.model.SearchSuggestions
import com.google.android.piyush.youtube.model.VideoInfo
import com.google.android.piyush.youtube.repository.YoutubeRepository
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YoutubeViewModel @Inject constructor() : ViewModel() {

    private val repository: YoutubeRepository = YoutubeRepositoryImpl()
    private val _trendingVideos : MutableLiveData<Response<BrowseResponse>> = MutableLiveData()
    val trendingVideos : LiveData<Response<BrowseResponse>> = _trendingVideos

    private val _musicVideos : MutableLiveData<Response<BrowseResponse>> = MutableLiveData()
    val musicVideos : LiveData<Response<BrowseResponse>> = _musicVideos

    private val _gamingVideos : MutableLiveData<Response<BrowseResponse>> = MutableLiveData()
    val gamingVideos : LiveData<Response<BrowseResponse>> = _gamingVideos

    private val _moviesVideos : MutableLiveData<Response<BrowseResponse>> = MutableLiveData()
    val moviesVideos : LiveData<Response<BrowseResponse>> = _moviesVideos

    private val _playerInfo : MutableLiveData<Response<PlayerResponse>> = MutableLiveData()
    val playerInfo : LiveData<Response<PlayerResponse>> = _playerInfo

    private val _sharedVideoInfo : MutableLiveData<Response<VideoInfo>> = MutableLiveData()
    val sharedVideoInfo : LiveData<Response<VideoInfo>> = _sharedVideoInfo

    private val _searchKeys : MutableLiveData<List<String>>? = MutableLiveData()
    val searchKeys : LiveData<List<String>>? = _searchKeys

    private val _searchSuggestions : MutableLiveData<Response<SearchSuggestions>> = MutableLiveData()
    val searchSuggestions : LiveData<Response<SearchSuggestions>> = _searchSuggestions

    private val _relativeResults : MutableLiveData<MutableList<ReelShelfRenderer.Item.ShortsLockupViewModel>> = MutableLiveData()
    val relativeResults : LiveData<MutableList<ReelShelfRenderer.Item.ShortsLockupViewModel>> = _relativeResults

    private val _searchResults : MutableLiveData<Response<SearchResponse>> = MutableLiveData()
    val searchResults : LiveData<Response<SearchResponse>> = _searchResults

    private val _channelInfo : MutableLiveData<Response<ChannelResponse>> = MutableLiveData()
    val channelInfo : LiveData<Response<ChannelResponse>> = _channelInfo

    private val _channelHomeContent : MutableLiveData<Response<BrowseResponse>> = MutableLiveData()
    val channelHomeContent : LiveData<Response<BrowseResponse>> = _channelHomeContent

    private var alreadyExistsData = false

    init {
        getTrendingVideos()
    }

    fun keys(keys : List<String>) = viewModelScope.launch {
        _searchKeys?.postValue(keys)
    }

    fun relativeResults(results : MutableList<ReelShelfRenderer.Item.ShortsLockupViewModel>) = viewModelScope.launch {
        _relativeResults.postValue(results)
    }

    fun searchSuggestions(query : String) = viewModelScope.launch {
        _searchSuggestions.postValue(Response.Loading)
        try {
            val suggestions = repository.searchSuggestions(query)
            _searchSuggestions.postValue(Response.Success(suggestions))
        }catch (e : Exception) {
            _searchSuggestions.postValue(Response.Error(e))
        }
    }

    fun searchData(query : String) = viewModelScope.launch {
        _searchResults.postValue(Response.Loading)
        try {
            val results = repository.searchResults(query)
            results.let {
                _searchResults.postValue(Response.Success(it))
            }
        } catch (e : Exception) {
            _searchResults.postValue(Response.Error(e))
        }
    }

    fun submitSharedVideoInfo(videoInfo: VideoInfo) {
        try {
            _sharedVideoInfo.postValue(Response.Success(videoInfo))
        } catch (e : Exception) {
            _sharedVideoInfo.postValue(Response.Error(e))
        }
    }

    fun getPlayerInfo(videoId : String) = viewModelScope.launch {
        _playerInfo.postValue(Response.Loading)
        try {
            val info = repository.playerInfo(videoId)
            _playerInfo.postValue(Response.Success(info))
        } catch (e : Exception) {
            _playerInfo.postValue(Response.Error(e))
        }
    }

    fun channelDetails(channelId : String, filter : String?) = viewModelScope.launch {
        _channelInfo.postValue(Response.Loading)
        try {
            val info = repository.channelResponse(channelId, filter)
            _channelInfo.postValue(Response.Success(info))
        } catch (e : Exception) {
            _channelInfo.postValue(Response.Error(e))
        }
    }

    fun channelHomeContent(browseId : String) = viewModelScope.launch {
        _channelHomeContent.postValue(Response.Loading)
        try {
            val content = repository.browseNow(browseId = browseId, params = CHANNEL_HOME)
            _channelHomeContent.postValue(Response.Success(content))
        } catch (e : Exception) {
            _channelHomeContent.postValue(Response.Error(e))
        }
    }

    private fun getTrendingVideos() = viewModelScope.launch {
       if(!alreadyExistsData){
           _trendingVideos.postValue(Response.Loading)
           _moviesVideos.postValue(Response.Loading)
           _gamingVideos.postValue(Response.Loading)
           _musicVideos.postValue(Response.Loading)
           try {
               val now = repository.browseNow(TRENDING, null)
               val music = repository.browseMusic(TRENDING)
               val gaming = repository.browseGaming(TRENDING)
               val movies = repository.browseMovies(TRENDING)
               _trendingVideos.postValue(Response.Success(now))
               _musicVideos.postValue(Response.Success(music))
               _gamingVideos.postValue(Response.Success(gaming))
               _moviesVideos.postValue(Response.Success(movies))
               alreadyExistsData = true
           } catch (e : Exception) {
               _trendingVideos.postValue(Response.Error(e))
               _musicVideos.postValue(Response.Error(e))
               _gamingVideos.postValue(Response.Error(e))
               _moviesVideos.postValue(Response.Error(e))
           }
       }
    }
}