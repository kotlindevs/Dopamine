package com.google.android.piyush.database.viewModel

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
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
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.database.model.CustomPlaylists
import com.google.android.piyush.database.repository.DopamineDatabaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class DatabaseViewModel(
    context: Context
) : ViewModel() {

    private val dopamineDatabaseRepository : DopamineDatabaseRepository
    private val database = DopamineDatabase.getDatabase(context).openHelper
    private val currentUser = FirebaseAuth.getInstance().currentUser

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
    //Custom Playlist
    val defaultMasterDev = database.writableDatabase.execSQL("CREATE TABLE IF NOT EXISTS DopamineMastersDev (playlistName TEXT PRIMARY KEY, playlistDescription TEXT)")
    fun createCustomPlaylist(playlistsData: CustomPlaylistView) {
        val writableDatabase = database.writableDatabase
        val newPlaylistName = stringify(playlistsData.playListName)
        val query = "CREATE TABLE IF NOT EXISTS $newPlaylistName (videoId TEXT PRIMARY KEY, title TEXT, customName TEXT, thumbnail TEXT, channelId TEXT)"
        writableDatabase.execSQL("CREATE TABLE IF NOT EXISTS DopamineMastersDev (playlistName TEXT PRIMARY KEY, playlistDescription TEXT)")
        writableDatabase.execSQL("INSERT INTO DopamineMastersDev VALUES (\"${stringify(playlistsData.playListName)}\",\"${playlistsData.playListDescription}\") ")
        writableDatabase.execSQL(query)
    }

    fun addItemsInCustomPlaylist(playlistName: String,playlistsData: CustomPlaylists) {
        val writableDatabase = database.writableDatabase
        val newPlaylistName = stringify(playlistName)
        val query = "INSERT INTO $newPlaylistName VALUES (\"${playlistsData.videoId}\",\"${playlistsData.title}\",\"${playlistsData.customName}\",\"${playlistsData.thumbnail}\",\"${playlistsData.channelId}\")"
        writableDatabase.execSQL(query)
    }

    private val usersFavoritePlayListName = currentUser?.displayName+" Favorites"
    val newPlaylistName = stringify(usersFavoritePlayListName)


    fun defaultUserPlaylist() {
        val usersFavoritePlayListDescription =  "Your favorites playlist can be found in library"
        val writableDatabase = database.writableDatabase
        writableDatabase.execSQL("CREATE TABLE IF NOT EXISTS $newPlaylistName (videoId TEXT PRIMARY KEY, title TEXT, customName TEXT, thumbnail TEXT, channelId TEXT)")
        writableDatabase.execSQL("INSERT INTO DopamineMastersDev VALUES (\"$newPlaylistName\",\"$usersFavoritePlayListDescription\")")
    }


    fun getPlaylist() : List<CustomPlaylistView>{
        val writableDatabase = database.writableDatabase
        val list = mutableListOf<CustomPlaylistView>()
        val data  = writableDatabase.query("SELECT * FROM DopamineMastersDev ORDER BY playlistName ASC")
        while (data.moveToNext()){
            list.add(
                CustomPlaylistView(
                    stringify(
                        data.getString(0)
                    ),
                    data.getString(1)
                )
            )
        }
        return list
    }

    fun getPlaylistsFromDatabase() : List<String>{
        val writableDatabase = database.writableDatabase
        val list = mutableListOf<String>()
        val query = "SELECT name FROM sqlite_master Where type=\"table\" except \n" +
                "select name from sqlite_master where name=\"android_metadata\" Except  select name from sqlite_master where name= \"recent_videos\" except  select name from sqlite_master where name= \"room_master_table\" except  \n" +
                " select name from sqlite_master where name= \"sqlite_sequence\" except  select name from sqlite_master where name= \"search_table\" except  select name from sqlite_master where name= \"favourite_videos\" except  select name from sqlite_master where name= \"DopamineMastersDev\" "
        val data  = writableDatabase.query(query)
        while(data.moveToNext()){
            list.add(
                stringify(data.getString(0))
            )
            Log.d("list",list.toString())
        }
        return list
    }

    fun isExistsDataInPlaylist(playlistName: String,videoId: String) : Boolean {
        val writableDatabase = database.writableDatabase
        val newPlaylistName = stringify(playlistName)
        val query = "SELECT videoId FROM $newPlaylistName WHERE videoId = \"$videoId\" "
        val data = writableDatabase.query(query)
        while (data.moveToNext()) {
            val dbTableVideoId= data.getString(0)
            if(dbTableVideoId == videoId){
                return true
            }
        }
        return false
    }

    fun countTheNumberOfCustomPlaylist() : Int {
        val writableDatabase = database.writableDatabase

        val query = "SELECT COUNT(*) FROM DopamineMastersDev"
        val data = writableDatabase.query(
            query
        )
        var count = 0
        while (data.moveToNext()){
            count = data.getInt(0)
            Log.d("NumberOfOPlaylistsInDatabase",count.toString())
        }
        return count
    }

    fun isPlaylistExist(playlistName : String) : Boolean {
        val writableDatabase = database.writableDatabase
        val query = writableDatabase.query("SELECT playlistName FROM DopamineMastersDev WHERE playlistName = \"$playlistName\" ")
        while (query.moveToNext()){
            val dbTableName = query.getString(0)
            if(query.getString(0).isNotEmpty()){
                if(dbTableName == playlistName){
                    return true
                }
            }
        }
        return false
    }

    private fun stringify(playlistName: String): String {
        val name = if(playlistName.isNotEmpty()){
            if(playlistName.contains(" ")){
                playlistName.replace(" ","_")
            } else{
                playlistName.replace("_"," ")
            }
        } else {
            "Null"
        }

        val nameRecheck = if(name.contains('(') && name.contains(')')){
            name.replace('(','_').replace(')','_')
        }else{
          return name
        }
        return nameRecheck
    }
}