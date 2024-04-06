package com.google.android.piyush.dopamine.viewModels

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
                                "playListName" to newPlayListName
                                ,"playListDescription" to playListDescription
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

    fun addInCustomPlaylists(playlist : CustomPlaylists){
        currentUser?.let { user ->
            val valueEventListener =  object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { snap ->
                        if (snap.key == playlist.videoId) {
                            _customPlaylist.value =
                                RealtimeResource.Success(snap.getValue(CustomPlaylists::class.java)!!)
                        }
                    }

                    if (snapshot.childrenCount.toInt() == 0) {
                        reference.child(user.uid).child("masterRecords").child(playlist.videoId!!)
                            .setValue(playlist)
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
}