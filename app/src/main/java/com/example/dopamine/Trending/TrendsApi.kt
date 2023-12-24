package com.example.dopamine.Trending

import com.example.dopamine.TracksList.TracksDataClass.Track
import retrofit2.Call
import retrofit2.http.GET

interface TrendsApi {
    @GET("tracks")
    fun getTrends() : Call<List<Trend>>
}