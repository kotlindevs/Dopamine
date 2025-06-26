package com.google.android.piyush.dopamine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.database.DopamineDao
import com.google.android.piyush.database.entities.RecentSearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DopamineDbViewModel
    @Inject constructor (
        private val dopamineDao: DopamineDao
) : ViewModel(){

    fun addSearchKeyword(keyword: String){
        viewModelScope.launch(Dispatchers.IO) {
            if(keyword.isNotBlank() && keyword.isNotEmpty()){
                val recentSearch = RecentSearch(
                    searchText = keyword
                )
                dopamineDao.addSearchKeyword(recentSearch)
            }
        }
    }
}