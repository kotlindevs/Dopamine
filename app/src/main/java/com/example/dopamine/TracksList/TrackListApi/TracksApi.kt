package com.example.dopamine.TracksList.TrackListApi

import com.example.dopamine.TracksList.TracksDataClass.Track
import retrofit2.Call
import retrofit2.http.GET

interface TracksApi {
    @GET("tracks")
    fun getTracks() : Call<List<Track>>
}