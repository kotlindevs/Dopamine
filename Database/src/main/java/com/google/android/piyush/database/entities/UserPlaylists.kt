package com.google.android.piyush.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity("user_playlists")
data class UserPlaylists @OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey
    val playlistId : String = Uuid.random().toString(),
    val playlistName : String,
    val playlistDescription : String,
)
