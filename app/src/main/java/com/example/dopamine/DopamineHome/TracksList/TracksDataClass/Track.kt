package com.example.dopamine.DopamineHome.TracksList.TracksDataClass

data class Track(
    val id: String,
    val artist_name: String,
    val song_name: String,
    val type: String,
    val is_playable: Boolean,
    val rc_url  : String,
    val mp_url: String,
    val preview_url: String,
    val release_date: String
)