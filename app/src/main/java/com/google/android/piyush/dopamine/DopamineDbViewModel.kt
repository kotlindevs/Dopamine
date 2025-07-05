package com.google.android.piyush.dopamine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.database.DopamineDao
import com.google.android.piyush.database.entities.FavouriteChannels
import com.google.android.piyush.database.entities.RecentSearch
import com.google.android.piyush.database.entities.RecentlyExplored
import com.google.android.piyush.database.entities.User
import com.google.android.piyush.database.entities.UserPlaylists
import com.google.android.piyush.youtube.utilities.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DopamineDbViewModel
    @Inject constructor (
        private val dopamineDao: DopamineDao
) : ViewModel(){

    private val _recentSearch : MutableLiveData<Response<MutableList<RecentSearch>>> = MutableLiveData(Response.Loading)
    val recentSearch: LiveData<Response<MutableList<RecentSearch>>> = _recentSearch

    private val _getRecentWatchHistory : MutableLiveData<MutableList<RecentlyExplored>?> = MutableLiveData()
    val getRecentWatchHistory: LiveData<MutableList<RecentlyExplored>?> = _getRecentWatchHistory

    private val _getUser : MutableLiveData<User?> = MutableLiveData()
    val getUser: LiveData<User?> = _getUser

    private val _userPlaylists : MutableLiveData<MutableList<UserPlaylists>?> = MutableLiveData()
    val userPlaylists: LiveData<MutableList<UserPlaylists>?> = _userPlaylists

    private val _isChannelFavourite : MutableLiveData<FavouriteChannels?> = MutableLiveData()
    val isChannelFavourite: LiveData<FavouriteChannels?> = _isChannelFavourite

    init {
        getUser()
        getRecentWatchHistory()
        getAllUserPlaylists()
    }

    private fun getRecentWatchHistory() = viewModelScope.launch {
        try {
            val videos = dopamineDao.getAllRecentlyExplored()
            _getRecentWatchHistory.postValue(videos)
        }catch (e: Exception){
            e.printStackTrace()
            _getRecentWatchHistory.postValue(mutableListOf())
        }
    }

    fun loadRecentSearch() = viewModelScope.launch(Dispatchers.IO) {
        _recentSearch.postValue(Response.Loading)
        try {
            val results = dopamineDao.getSearchKeywords()
            _recentSearch.postValue(Response.Success(results))
        } catch (e: Exception){
            _recentSearch.postValue(Response.Error(e))
        }
    }

    fun addSearchKeyword(keyword: String, timestamp: Long){
        viewModelScope.launch(Dispatchers.IO) {
            if(keyword.isNotBlank() && keyword.isNotEmpty()){
                val recentSearch = RecentSearch(
                    searchText = keyword,
                    timestamp = timestamp
                )
                dopamineDao.addSearchKeyword(recentSearch)
            }
        }
    }

    fun deleteRecentSearch(keyword: String) = viewModelScope.launch(Dispatchers.IO) {
        dopamineDao.deleteSearchKeyword(keyword)
        loadRecentSearch()
    }

    fun setUser(user: User) = viewModelScope.launch {
        dopamineDao.setUser(user)
    }

    fun addRecentWatch(video : RecentlyExplored?) = viewModelScope.launch {
        video?.let {
            dopamineDao.addRecentlyExplored(video)
        }
    }
    private fun getUser() = viewModelScope.launch {
        val user = dopamineDao.getUser()
        _getUser.postValue(user)
    }

    fun createUserPlaylist(playlists: UserPlaylists) = viewModelScope.launch {
        try {
            dopamineDao.createUserPlaylists(userPlaylists = playlists)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun addFavouriteChannels(channel : FavouriteChannels) = viewModelScope.launch {
        try {
            dopamineDao.addFavouriteChannels(channel)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun isChannelFavourite(channelId: String) = viewModelScope.launch {
        try {
            val channel = dopamineDao.isChannelFavourite(channelId)
            _isChannelFavourite.postValue(channel)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun getAllUserPlaylists() {
        viewModelScope.launch {
            try {
                val playlists = dopamineDao.getAllPlaylists()
                _userPlaylists.postValue(playlists)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}