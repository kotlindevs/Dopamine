package com.example.dopamine.DopamineHome.Trending

import retrofit2.Call
import retrofit2.http.GET

interface TrendsApi {
    @GET("tracks")
    fun getTrends() : Call<List<Trend>>
}