package com.google.android.piyush.dopamine.viewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.piyush.dopamine.authentication.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RealtimeViewModel : ViewModel() {

    private val database = Firebase.database
    private val reference = database.reference

    private val _dopamineUser : MutableLiveData<RealtimeResource<User>> = MutableLiveData()
    val dopamineUser : LiveData<RealtimeResource<User>> = _dopamineUser

    fun isUserExists(dopamineUser : User) {
        reference.child(dopamineUser.userId!!).child("userDetails").addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if(it.key == dopamineUser.userId){
                            _dopamineUser.value = RealtimeResource.Success(it.getValue(User::class.java)!!)
                        }else{
                            reference.child(dopamineUser.userId).child("userDetails").setValue(dopamineUser)
                        }
                        val totalUsers = snapshot.childrenCount
                        Log.d(TAG, "totalUsers: $totalUsers")
                    }
                    if(snapshot.childrenCount.toInt() == 0){
                        reference.child(dopamineUser.userId).child("userDetails").setValue(dopamineUser)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    RealtimeResource.Error(
                        data = null,
                        message = error.message
                    )
                }
            }
        )
    }
}