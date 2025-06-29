package com.google.android.piyush.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("user")
data class User(
    @PrimaryKey
    val userId : String = "dopamine_user",
    val userName : String? = null,
    val userDescription : String? = null
)
