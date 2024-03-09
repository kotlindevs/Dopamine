package com.google.android.piyush.database.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.database.dao.DopamineDao
import com.google.android.piyush.database.DopamineDatabase
import com.google.android.piyush.database.entities.EntityFavouritePlaylist
import com.google.android.piyush.database.entities.EntityRecentVideos
import com.google.android.piyush.database.entities.EntityVideoSearch
import com.google.android.piyush.database.repository.DopamineDatabaseRepository
import kotlinx.coroutines.launch

class DatabaseViewModel(
    context: Context
) : ViewModel() {

    private val dopamineDatabaseRepository : DopamineDatabaseRepository

    private val _searchVideoHistory = MutableLiveData<List<EntityVideoSearch>>()
    val searchVideoHistory : LiveData<List<EntityVideoSearch>> = _searchVideoHistory

    private val _favouritePlayList = MutableLiveData<List<EntityFavouritePlaylist>>()
    val favouritePlayList : LiveData<List<EntityFavouritePlaylist>> = _favouritePlayList

    private val _isFavourite : MutableLiveData<String> = MutableLiveData()
    val isFavourite : LiveData<String> = _isFavourite

    private val _recentVideos : MutableLiveData<List<EntityRecentVideos>> = MutableLiveData()
    val recentVideos : LiveData<List<EntityRecentVideos>> = _recentVideos

    private val _isRecent : MutableLiveData<String> = MutableLiveData()
    val isRecent : LiveData<String> = _isRecent

    init {
        val dopamineDao = DopamineDatabase.getDatabase(context).dopamineDao()
        dopamineDatabaseRepository = DopamineDatabaseRepository(dopamineDao)
    }

    fun insertSearchVideos(searchVideos: EntityVideoSearch) {
        viewModelScope.launch {
            dopamineDatabaseRepository.insertSearchVideos(searchVideos)
        }
    }
    fun deleteSearchVideoList() {
        viewModelScope.launch {
            dopamineDatabaseRepository.deleteSearchVideoList()
        }
    }
    fun getSearchVideoList() {
        viewModelScope.launch {
           _searchVideoHistory.value = dopamineDatabaseRepository.getSearchVideoList()
        }
    }

    fun insertFavouriteVideos(favouritePlaylist: EntityFavouritePlaylist) {
        viewModelScope.launch {
            dopamineDatabaseRepository.insertFavouriteVideos(favouritePlaylist)
        }
    }

    fun isFavouriteVideo(videoId: String) : String {
        viewModelScope.launch {
          _isFavourite.value =  dopamineDatabaseRepository.isFavouriteVideo(videoId)
        }
        return _isFavourite.value.toString()
    }

    fun deleteFavouriteVideo(videoId: String) {
        viewModelScope.launch {
            dopamineDatabaseRepository.deleteFavouriteVideo(videoId)
        }
    }

    fun getFavouritePlayList() {
        viewModelScope.launch {
           _favouritePlayList.value = dopamineDatabaseRepository.getFavouritePlayList()
        }
    }

    fun insertRecentVideos(recentVideos: EntityRecentVideos) {
        viewModelScope.launch {
            dopamineDatabaseRepository.insertRecentVideos(recentVideos)
        }
    }

    fun getRecentVideos() {
        viewModelScope.launch {
            _recentVideos.value = dopamineDatabaseRepository.getRecentVideos()
        }
    }

    fun isRecentVideo(videoId: String) : String {
        viewModelScope.launch {
           _isRecent.value = dopamineDatabaseRepository.isRecentVideo(videoId)
        }
        return _isRecent.value.toString()
    }

    fun updateRecentVideo(videoId: String,time : String) {
        viewModelScope.launch {
            dopamineDatabaseRepository.updateRecentVideo(videoId,time)
        }
    }
}