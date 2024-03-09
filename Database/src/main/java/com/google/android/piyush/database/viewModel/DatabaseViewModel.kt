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

    init {
        val dopamineDao = DopamineDatabase.getDatabase(context).dopamineDao()
        dopamineDatabaseRepository = DopamineDatabaseRepository(dopamineDao)
        viewModelScope.launch {
            _searchVideoHistory.value = getSearchVideoList()
        }
    }

    fun insertSearchVideos(searchVideos: EntityVideoSearch) = viewModelScope.launch {
        dopamineDatabaseRepository.insertSearchVideos(searchVideos)
    }

    fun deleteSearchVideoList() = viewModelScope.launch {
        dopamineDatabaseRepository.deleteSearchVideoList()
    }
    private suspend fun getSearchVideoList() : List<EntityVideoSearch> {
        return dopamineDatabaseRepository.getSearchVideoList()
    }

    fun insertFavouriteVideos(favouritePlaylist: EntityFavouritePlaylist) = viewModelScope.launch {
        dopamineDatabaseRepository.insertFavouriteVideos(favouritePlaylist)
    }

    fun isFavouriteVideo(videoId: String) = viewModelScope.launch {
        dopamineDatabaseRepository.isFavouriteVideo(videoId)
    }

    fun deleteFavouriteVideo(videoId: String) = viewModelScope.launch {
        dopamineDatabaseRepository.deleteFavouriteVideo(videoId)
    }

    fun getFavouritePlayList() = viewModelScope.launch {
        dopamineDatabaseRepository.getFavouritePlayList()
    }

    fun insertRecentVideos(recentVideos: EntityRecentVideos) = viewModelScope.launch {
        dopamineDatabaseRepository.insertRecentVideos(recentVideos)
    }

    fun getRecentVideos() = viewModelScope.launch {
        dopamineDatabaseRepository.getRecentVideos()
    }

    fun isRecentVideo(videoId: String) = viewModelScope.launch {
        dopamineDatabaseRepository.isRecentVideo(videoId)
    }

    fun updateRecentVideo(videoId: String,time : String) = viewModelScope.launch {
        dopamineDatabaseRepository.updateRecentVideo(videoId,time)
    }
}