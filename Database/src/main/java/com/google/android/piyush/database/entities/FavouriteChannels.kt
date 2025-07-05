package com.google.android.piyush.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_channels")
data class FavouriteChannels(
    @PrimaryKey
    val channelId : String,
    val channelName : String? = null,
    val channelImage : String? = null,
    val channelSubscribersCount : String? = null,
    val channelShortUrl : String? = null
)