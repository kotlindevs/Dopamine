package com.google.android.piyush.dopamine.viewModels

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.piyush.database.entities.EntityFavouritePlaylist
import com.google.android.piyush.database.entities.EntityRecentVideos
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.database.model.CustomPlaylists
import com.google.android.piyush.dopamine.authentication.User
import com.google.android.piyush.dopamine.utilities.dopamineSharedPreferences
import com.google.android.piyush.youtube.utilities.Notifications
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RealtimeViewModel : ViewModel() {

    private val firebase = Firebase
    private val database = firebase.database
    private val reference = database.reference
    private val auth = firebase.auth
    private val currentUser = auth.currentUser

    private val _dopamineUser : MutableLiveData<RealtimeResource<User>> = MutableLiveData()
    val dopamineUser : LiveData<RealtimeResource<User>> = _dopamineUser

    private val _recentVideos : MutableLiveData<RealtimeResource<EntityRecentVideos>> = MutableLiveData()
    val recentVideos : LiveData<RealtimeResource<EntityRecentVideos>> = _recentVideos

    private val _listOfRecentVideos : MutableLiveData<RealtimeResource<List<EntityRecentVideos>>> = MutableLiveData()
    val listOfRecentVideos : LiveData<RealtimeResource<List<EntityRecentVideos>>> = _listOfRecentVideos

    private val _notifications : MutableLiveData<RealtimeResource<Notifications>> = MutableLiveData()
    val notifications : LiveData<RealtimeResource<Notifications>> = _notifications

    private val _listOfNotifications : MutableLiveData<RealtimeResource<List<Notifications>>> = MutableLiveData()
    val listOfNotifications : LiveData<RealtimeResource<List<Notifications>>> = _listOfNotifications

    private val _customPlaylistView : MutableLiveData<RealtimeResource<CustomPlaylistView>> = MutableLiveData()
    val customPlaylistView : LiveData<RealtimeResource<CustomPlaylistView>> = _customPlaylistView

    private val _listOfCustomPlaylistView : MutableLiveData<RealtimeResource<List<CustomPlaylistView>>> = MutableLiveData()
    val listOfCustomPlaylistView : LiveData<RealtimeResource<List<CustomPlaylistView>>> = _listOfCustomPlaylistView

    private val _customPlaylist : MutableLiveData<RealtimeResource<CustomPlaylists>> = MutableLiveData()
    val customPlaylist : LiveData<RealtimeResource<CustomPlaylists>> = _customPlaylist

    private val _favorites : MutableLiveData<RealtimeResource<String>> = MutableLiveData()
    val favorites : LiveData<RealtimeResource<String>> = _favorites

    private val _countTheMasterRecords : MutableLiveData<RealtimeResource<Int>> = MutableLiveData()
    val countTheMasterRecords : LiveData<RealtimeResource<Int>> = _countTheMasterRecords

    private val _getPlaylistData : MutableLiveData<RealtimeResource<List<CustomPlaylists>>> = MutableLiveData()
    val getPlaylistData : LiveData<RealtimeResource<List<CustomPlaylists>>> = _getPlaylistData

    private val _getALlPlaylists : MutableLiveData<RealtimeResource<List<String>>> = MutableLiveData()
    val getAllPlaylists : LiveData<RealtimeResource<List<String>>> = _getALlPlaylists

    private val _isVideoExists : MutableLiveData<RealtimeResource<Boolean>> = MutableLiveData()
    val isVideoExists : LiveData<RealtimeResource<Boolean>> = _isVideoExists

    private val _isPlaylistExists : MutableLiveData<RealtimeResource<Boolean>> = MutableLiveData()
    val isPlaylistExists : LiveData<RealtimeResource<Boolean>> = _isPlaylistExists

    fun isUserExists(dopamineUser : User) {

        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if (it.key == dopamineUser.userId) {
                            _dopamineUser.value =
                                RealtimeResource.Success(it.getValue(User::class.java)!!)
                        } else {
                            reference.child(user.uid).child("userDetails")
                                .setValue(dopamineUser)
                        }
                    }
                    if (snapshot.childrenCount.toInt() == 0) {
                        reference.child(user.uid).child("userDetails")
                            .setValue(dopamineUser)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
            reference.child(dopamineUser.userId!!).child("userDetails")
                .addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }


    fun isRecentVideos(recentVideos : EntityRecentVideos){

        currentUser?.let {
            val valueEventListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if(it.key == recentVideos.videoId){
                            _recentVideos.value = RealtimeResource.Success(it.getValue(EntityRecentVideos::class.java)!!)
                        }else{
                            reference.child(currentUser.uid).child("recentVideos").child(recentVideos.videoId!!).setValue(recentVideos)
                        }
                    }
                    if(snapshot.childrenCount.toInt() == 0){
                        reference.child(currentUser.uid).child("recentVideos").child(recentVideos.videoId!!).setValue(recentVideos)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }

            reference.child(currentUser.uid).child("recentVideos").addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun getRecentVideos() {
        val recentVideos =  mutableListOf<EntityRecentVideos>()
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { videos ->
                        recentVideos.add(
                            videos.getValue(
                                EntityRecentVideos::class.java
                            )!!
                        )
                    }
                    _listOfRecentVideos.value = RealtimeResource.Success(recentVideos)
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }

            reference.child(user.uid).child("recentVideos").addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun updateRecentVideos(videoId : String, timing : String){

        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if(it.key == videoId){
                            val update = mapOf(
                                "timing" to timing
                            )
                            it.ref.updateChildren(update)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
            reference.child(user.uid).child("recentVideos").addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun sendNotification(notifications: Notifications,context: Context) {
        val adminId = dopamineSharedPreferences(context = context).getString("adminId", "")
        val notificationId = notifications.id.toString()
        adminId?.let{
            val valueEventListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if(it.key == notifications.id.toString()){
                            _notifications.value = RealtimeResource.Success(it.getValue(Notifications::class.java)!!)
                        }else{
                            reference.child(adminId).child("notifications").child(notificationId).setValue(notifications)
                        }
                        val totalNotifications = snapshot.childrenCount
                        Log.d(TAG, "totalNotifications: $totalNotifications")
                    }
                    if(snapshot.childrenCount.toInt() == 0){
                        reference.child(adminId).child("notifications").child(notificationId).setValue(notifications)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }

            reference.child(adminId).child("notifications").addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun getNotifications(context: Context) {
        val notifications =  mutableListOf<Notifications>()
        val adminId = dopamineSharedPreferences(context = context).getString("adminId", "")
        adminId?.let {

            val valueEventListener =  object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { notification ->
                        notifications.add(
                            notification.getValue(
                                Notifications::class.java
                            )!!
                        )
                    }
                    _listOfNotifications.value = RealtimeResource.Success(notifications)
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }

            reference.child(adminId).child("notifications").addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun deleteNotification(notificationId: Int, context: Context){
        val adminId = dopamineSharedPreferences(context = context).getString("adminId", "")

        adminId?.let {
            reference.child(adminId).child("notifications").child(notificationId.toString()).removeValue()
        }
    }

    fun clearAllWatchHistory() {
        currentUser?.let { user ->
            reference.child(user.uid).child("recentVideos").removeValue()
        }
    }

    fun deleteUserDetails() {
        currentUser?.let { user ->
            reference.child(user.uid).child("userDetails").removeValue()
        }
    }

    fun deleteYourAccount() {
        currentUser?.let { user ->
            reference.child(user.uid).removeValue()
        }
    }

    fun addInMasterRecords (playlist: CustomPlaylistView) {
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { snap ->
                        if (snap.key == playlist.playListName) {
                            _customPlaylistView.value =
                                RealtimeResource.Success(snap.getValue(CustomPlaylistView::class.java)!!)
                        } else {
                            playlist.playListName?.let {
                                reference.child(user.uid).child("masterRecords").child(it)
                                    .setValue(playlist)
                                reference.child(user.uid).child(it).setValue(playlist)
                            }
                        }
                    }

                    if (snapshot.childrenCount.toInt() == 0) {
                        playlist.playListName?.let {
                            reference.child(user.uid).child("masterRecords").child(it)
                                .setValue(playlist)
                            reference.child(user.uid).child(it).setValue(playlist)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
            reference.child(user.uid).child("masterRecords")
                .addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun getMasterRecords() {
        val customPlaylists =  mutableListOf<CustomPlaylistView>()
        currentUser?.let { user ->

            val valueEventListener =  object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { playlist ->
                        customPlaylists.add(
                            playlist.getValue(
                                CustomPlaylistView::class.java
                            )!!
                        )
                    }
                    _listOfCustomPlaylistView.value = RealtimeResource.Success(customPlaylists)
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }

            reference.child(user.uid).child("masterRecords").addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun updateMasterRecords(oldPlayListName : String, newPlayListName : String ,playListDescription : String) {
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if(it.key == oldPlayListName){
                            val update = mapOf(
                                "playListName" to newPlayListName,
                                "playListDescription" to playListDescription
                            )
                            it.ref.updateChildren(update)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
            reference.child(user.uid).child("masterRecords").addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun deleteMasterRecords(playListName: String) {
        currentUser?.let { user ->
            reference.child(user.uid).child("masterRecords").child(playListName).removeValue()
        }
    }

    fun addYourFavorites(favoritesVideos : EntityFavouritePlaylist){
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if (it.key == favoritesVideos.videoId) {
                           // Log.d(TAG, "onDataChange: ${it.key}")
                        } else {
                            reference.child(user.uid).child("favoritePlaylist").child(
                                favoritesVideos.videoId!!
                            ).setValue(favoritesVideos)
                        }
                    }
                    if (snapshot.childrenCount.toInt() == 0) {
                        reference.child(user.uid).child("favoritePlaylist").child(
                            favoritesVideos.videoId!!
                        ).setValue(favoritesVideos)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
            reference.child(user.uid).child("favoritePlaylist")
                .addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun deleteYourFavorites(video : String) {
        currentUser?.let { user ->
            reference.child(user.uid).child("favoritePlaylist").child(video).removeValue()
        }
    }

    fun deletePlaylist(playlist : String) {
        currentUser?.let { user ->
            reference.child(user.uid).child(playlist).removeValue()
        }
    }

    fun isFavorite(videoId : String){
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if (it.key == videoId) {
                            _favorites.value = RealtimeResource.Success(it.key!!)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
            reference.child(user.uid).child("favoritePlaylist")
                .addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun countingMasterRecords () {
        currentUser?.let {
            val valueEventListener =  object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _countTheMasterRecords.value = RealtimeResource.Success(snapshot.childrenCount.toInt())
                }
                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                    Log.d(TAG, "onCancelled: ${error.message}")
                }
            }
            reference.child(it.uid).child("masterRecords")
                .addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun updateYourPlaylist(oldPlayListName : String, newPlayListName: String) {
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if(it.key == oldPlayListName){
                            val update = mapOf(
                                it.key to newPlayListName
                            )
                            it.ref.updateChildren(update)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
            reference.child(user.uid).child(oldPlayListName).addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun getPlaylistVideos(playListName : String) {
        val customPlaylists =  mutableListOf<CustomPlaylists>()
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { videos ->
                        customPlaylists.add(
                            videos.getValue(
                                CustomPlaylists::class.java
                            )!!
                        )
                    }
                    _getPlaylistData.value = RealtimeResource.Success(customPlaylists)
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }

            reference.child(user.uid).child(playListName).addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun initializeFavorites(playlist: CustomPlaylistView) {
        currentUser?.let { user ->
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { snap ->
                        if (snap.key == playlist.playListName) {
                            _customPlaylistView.value =
                                RealtimeResource.Success(snap.getValue(CustomPlaylistView::class.java)!!)
                        } else {
                            playlist.playListName?.let {
                                reference.child(user.uid).child("masterRecords").child(it)
                                    .setValue(playlist)
                            }
                        }
                    }

                    if (snapshot.childrenCount.toInt() == 0) {
                        playlist.playListName?.let {
                            reference.child(user.uid).child("masterRecords").child(it)
                                .setValue(playlist)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
            reference.child(user.uid).child("masterRecords")
                .addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun initializeWatchLater(playlist: CustomPlaylistView) {
        currentUser?.let { user ->
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { snap ->
                        if (snap.key == playlist.playListName) {
                            _customPlaylistView.value =
                                RealtimeResource.Success(snap.getValue(CustomPlaylistView::class.java)!!)
                        } else {
                            playlist.playListName?.let {
                                reference.child(user.uid).child("masterRecords").child(it)
                                    .setValue(playlist)
                            }
                        }
                    }

                    if (snapshot.childrenCount.toInt() == 0) {
                        playlist.playListName?.let {
                            reference.child(user.uid).child("masterRecords").child(it)
                                .setValue(playlist)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
            reference.child(user.uid).child("masterRecords")
                .addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun getAllPlaylists() {
        val customPlaylists =  mutableListOf<String>()

        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { playlists ->
                        if(playlists.key != "userDetails" && playlists.key != "masterRecords" && playlists.key != "favoritePlaylist" && playlists.key != "recentVideos" && playlists.key != "notifications"){
                            customPlaylists.add(playlists.key!!)
                        }
                    }
                    _getALlPlaylists.value = RealtimeResource.Success(customPlaylists)
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }

            reference.child(user.uid).addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun isVideoExists(playlistName: String,videoId: String) {
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { playlists ->
                        if(playlists.key == playlistName){
                            playlists.children.forEach {
                                if (it.key == videoId) {
                                    _isVideoExists.value = RealtimeResource.Success(true)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }

            reference.child(user.uid).addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun addInYourPlaylist(playlists: CustomPlaylists,playlistName: String){
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if (it.key == playlists.videoId) {
                            // Log.d(TAG, "onDataChange: ${it.key}")
                        } else {
                            playlists.videoId?.let { videoId ->
                                reference.child(user.uid).child(playlistName).child(
                                    videoId
                                ).setValue(playlists)
                            }
                        }

                    }
                    if (snapshot.childrenCount.toInt() == 0) {
                        playlists.videoId?.let { videoId ->
                            reference.child(user.uid).child(playlistName).child(
                                videoId
                            ).setValue(playlists)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
            reference.child(user.uid).child(playlistName)
                .addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun deleteFromPlaylist(playlistName: String,videoId: String){
        currentUser?.let { user ->
            reference.child(user.uid).child(playlistName).child(videoId).removeValue()
        }
    }

    fun isPlaylistExists(playlistName: String){
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { playlists ->
                        if(playlists.key != "userDetails" && playlists.key != "masterRecords" && playlists.key != "favoritePlaylist" && playlists.key != "recentVideos" && playlists.key != "notifications"){
                            _isPlaylistExists.value = RealtimeResource.Success(true)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }

            reference.child(user.uid).addValueEventListener(valueEventListener)

            reference.removeEventListener(valueEventListener)
        }
    }

    fun updatePlaylist(playlists: CustomPlaylists, playListName: String) {
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if(it.key == playListName){
                            it.ref.removeValue()
                            it.ref.setValue(playlists)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
            reference.child(user.uid).child(playListName).addValueEventListener(valueEventListener)
            reference.removeEventListener(valueEventListener)
        }
    }
}