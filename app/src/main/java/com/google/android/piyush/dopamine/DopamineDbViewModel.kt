package com.google.android.piyush.dopamine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.database.DopamineDao
import com.google.android.piyush.database.entities.RecentSearch
import com.google.android.piyush.youtube.utilities.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DopamineDbViewModel
    @Inject constructor (
        private val dopamineDao: DopamineDao
) : ViewModel(){

    private val _recentSearch : MutableLiveData<Response<MutableList<RecentSearch>>> = MutableLiveData(Response.Loading)
    val recentSearch: LiveData<Response<MutableList<RecentSearch>>> = _recentSearch

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
}