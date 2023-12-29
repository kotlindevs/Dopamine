package com.example.dopamine.DopamineHome.OldButGold

import retrofit2.Call
import retrofit2.http.GET

interface ChartsApi {
    @GET("tracks")
    fun getCharts() : Call<List<Chart>>
}